package net.jules.core.scene;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI-generated .btlscene parser.
 * 
 */
public class SceneParser {

	private static final int SUPPORTED_VERSION = 1;
	private static final Set<String> KNOWN_TYPES = Set.of("PLANE");
	private static final Color DEFAULT_COLOR = Color.GRAY;

	private static final Pattern BRACKET = Pattern.compile("\\[([^\\]]*)\\]");
	private static final Pattern BRACKET_LIST =
			Pattern.compile("\\s*\\[[^\\]]*\\](\\s*,\\s*\\[[^\\]]*\\])*\\s*");

	private SceneParser() {
	}

	public static ArrayList<GeoBody> parseSceneFromFile(String filePath) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);

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

	private static GeoBody parseStatement(String statement) {
		String[] parts = statement.split("->");
		if (parts.length != 4 || !parts[0].trim().equals("GEOBODY")) {
			throw new IllegalArgumentException(
					"Expected GEOBODY->TYPE->(V...)->(T...), got: " + statement);
		}

		String type = parts[1].trim();
		if (!KNOWN_TYPES.contains(type)) {
			throw new IllegalArgumentException("Unknown body type '" + type + "' in: " + statement);
		}

		List<String[]> rawVerts = parseGroup(parts[2], "V", statement);
		List<String[]> rawTris = parseGroup(parts[3], "T", statement);

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
			triangles[i] = new Triangle3D(vertices[idx[0]], vertices[idx[1]], vertices[idx[2]], DEFAULT_COLOR);
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