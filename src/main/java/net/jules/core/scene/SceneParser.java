package net.jules.core.scene;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .btlscene parser.
 *
 * Statement format:
 *   GEOBODY->COLOR->TYPE->(V[x,y,z],...)->(T[i,j,k],...);
 * COLOR is a name (WHITE, RED, ...) or hex (#RRGGBB / 0xRRGGBB).
 * The COLOR token is optional: GEOBODY->TYPE->(V...)->(T...) still parses and uses DEFAULT_COLOR.
 */
public class SceneParser {

	private static final int SUPPORTED_VERSION = 1;
	private static final Set<String> KNOWN_TYPES = Set.of("PLANE");
	private static final Color DEFAULT_COLOR = Color.WHITE;

	private static final Map<String, Color> NAMED_COLORS = Map.ofEntries(
			Map.entry("WHITE", Color.WHITE),
			Map.entry("BLACK", Color.BLACK),
			Map.entry("RED", Color.RED),
			Map.entry("GREEN", Color.GREEN),
			Map.entry("BLUE", Color.BLUE),
			Map.entry("YELLOW", Color.YELLOW),
			Map.entry("CYAN", Color.CYAN),
			Map.entry("MAGENTA", Color.MAGENTA),
			Map.entry("ORANGE", Color.ORANGE),
			Map.entry("PINK", Color.PINK),
			Map.entry("GRAY", Color.GRAY),
			Map.entry("LIGHT_GRAY", Color.LIGHT_GRAY),
			Map.entry("DARK_GRAY", Color.DARK_GRAY));

	private static final Pattern BRACKET = Pattern.compile("\\[([^\\]]*)\\]");
	private static final Pattern BRACKET_LIST =
			Pattern.compile("\\s*\\[[^\\]]*\\](\\s*,\\s*\\[[^\\]]*\\])*\\s*");
	private static final Pattern HEX_COLOR = Pattern.compile("(?:#|0X)([0-9A-F]{6})");

	private SceneParser() {
	}

	/**
	 * @param resourcePath classpath path, e.g. "/btlscenes/scene1.btlscene"
	 */
	public static ArrayList<GeoBody> parseSceneFromFile(String resourcePath) throws IOException {
		List<String> lines = readResourceLines(resourcePath);

		Map<String, String> header = new HashMap<>();
		StringBuilder body = new StringBuilder();

		for (String raw : lines) {
			String line = raw.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			if (body.length() == 0 && !line.contains("->") && line.contains("=")) {
				int eq = line.indexOf('=');
				header.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
			} else {
				body.append(line).append(' ');
			}
		}

		checkVersion(header.get("BTLSCENEVERSION"));

		ArrayList<GeoBody> bodies = new ArrayList<>();
		for (String statement : body.toString().split(";")) {
			statement = statement.trim();
			if (!statement.isEmpty()) {
				bodies.add(parseStatement(statement));
			}
		}
		return bodies;
	}

	private static List<String> readResourceLines(String resourcePath) throws IOException {
		InputStream in = SceneParser.class.getResourceAsStream(resourcePath);
		if (in == null) {
			throw new FileNotFoundException("Not found on classpath: " + resourcePath);
		}
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines;
	}

	private static void checkVersion(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Missing BTLSCENEVERSION header");
		}
		int version;
		try {
			version = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid BTLSCENEVERSION: " + value);
		}
		if (version != SUPPORTED_VERSION) {
			throw new IllegalArgumentException(
					"Unsupported BTLSCENEVERSION " + version + " (expected " + SUPPORTED_VERSION + ")");
		}
	}

	private static Color parseColor(String token, String statement) {
		String t = token.trim().toUpperCase();
		Color named = NAMED_COLORS.get(t);
		if (named != null) {
			return named;
		}
		Matcher m = HEX_COLOR.matcher(t);
		if (m.matches()) {
			return new Color(Integer.parseInt(m.group(1), 16));
		}
		throw new IllegalArgumentException("Unknown color '" + token.trim() + "' in: " + statement
				+ " (use a name like RED or hex like #FF8800)");
	}

	private static GeoBody parseStatement(String statement) {
		String[] parts = statement.split("->");
		if ((parts.length != 4 && parts.length != 5) || !parts[0].trim().equals("GEOBODY")) {
			throw new IllegalArgumentException(
					"Expected GEOBODY->COLOR->TYPE->(V...)->(T...), got: " + statement);
		}

		// 5 parts: GEOBODY->COLOR->TYPE->V->T   |   4 parts (legacy): GEOBODY->TYPE->V->T
		boolean hasColor = parts.length == 5;
		Color color = hasColor ? parseColor(parts[1], statement) : DEFAULT_COLOR;
		int o = hasColor ? 1 : 0;

		String type = parts[1 + o].trim();
		if (!KNOWN_TYPES.contains(type)) {
			throw new IllegalArgumentException("Unknown body type '" + type + "' in: " + statement);
		}

		List<String[]> rawVerts = parseGroup(parts[2 + o], "V", statement);
		List<String[]> rawTris = parseGroup(parts[3 + o], "T", statement);

		Point3D[] vertices = new Point3D[rawVerts.size()];
		for (int i = 0; i < vertices.length; i++) {
			String[] c = rawVerts.get(i);
			if (c.length != 3) {
				throw new IllegalArgumentException("Vertex " + i + " needs 3 components in: " + statement);
			}
			try {
				vertices[i] = new Point3D(
						Float.parseFloat(c[0].trim()),
						Float.parseFloat(c[1].trim()),
						Float.parseFloat(c[2].trim()),
						0f, 0f);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Bad number in vertex " + i + " of: " + statement);
			}
		}

		// Triangles share the body's Point3D instances so move()/rotate*() affect them too.
		Triangle3D[] triangles = new Triangle3D[rawTris.size()];
		for (int i = 0; i < triangles.length; i++) {
			String[] c = rawTris.get(i);
			if (c.length != 3) {
				throw new IllegalArgumentException("Triangle " + i + " needs 3 indices in: " + statement);
			}
			int[] idx = new int[3];
			for (int j = 0; j < 3; j++) {
				try {
					idx[j] = Integer.parseInt(c[j].trim());
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Bad index in triangle " + i + " of: " + statement);
				}
				if (idx[j] < 0 || idx[j] >= vertices.length) {
					throw new IllegalArgumentException("Triangle " + i + " index " + idx[j]
							+ " out of range (0.." + (vertices.length - 1) + ") in: " + statement);
				}
			}
			triangles[i] = new Triangle3D(vertices[idx[0]], vertices[idx[1]], vertices[idx[2]], color);
		}

		GeoBody geoBody = new GeoBody(vertices);
		geoBody.setTriangles(triangles);
		return geoBody;
	}

	private static List<String[]> parseGroup(String group, String tag, String statement) {
		String g = group.trim();
		if (!g.startsWith("(") || !g.endsWith(")")) {
			throw new IllegalArgumentException("Group must be in parentheses in: " + statement);
		}
		String inner = g.substring(1, g.length() - 1).trim();
		if (!inner.startsWith(tag)) {
			throw new IllegalArgumentException("Group must start with '" + tag + "' in: " + statement);
		}
		inner = inner.substring(tag.length());
		if (!BRACKET_LIST.matcher(inner).matches()) {
			throw new IllegalArgumentException("Malformed " + tag + " group in: " + statement);
		}

		List<String[]> result = new ArrayList<>();
		Matcher m = BRACKET.matcher(inner);
		while (m.find()) {
			result.add(m.group(1).split(","));
		}
		return result;
	}
}