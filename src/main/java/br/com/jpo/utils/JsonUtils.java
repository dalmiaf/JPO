package br.com.jpo.utils;

import java.io.BufferedReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


public class JsonUtils {

	private static JsonParser jsonParser = new JsonParser();

	public static JsonObject parse(String value) {
		return jsonParser.parse(value).getAsJsonObject();
	}

	public static JsonObject parse(BufferedReader reader) {
		try {
			StringBuffer buffer = new StringBuffer();
			String line = null;

			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			return parse(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getAsString(JsonObject json, String attributeName) {
		if (json == null || StringUtils.getEmptyAsNull(attributeName) == null) {
			return null;
		}

		JsonPrimitive primitive = json.getAsJsonPrimitive(attributeName);

		if (primitive != null) {
			return primitive.getAsString();
		}

		return null;
	}

	public static JsonObject getAsJsonObject(JsonObject json, String elementName) {
		if (json == null || StringUtils.getEmptyAsNull(elementName) == null) {
			return null;
		}

		return json.getAsJsonObject(elementName);
	}

	public static JsonArray getAsJsonArray(JsonObject json, String elementName) {
		if (json == null || StringUtils.getEmptyAsNull(elementName) == null) {
			return null;
		}

		return json.getAsJsonArray(elementName);
	}

	public static void add(JsonArray array, JsonElement element) {
		if (array == null) {
			throw new IllegalArgumentException("Elemento JsonArray não pode ser nulo.");
		} else if (element == null) {
			throw new IllegalArgumentException("Elemento JsonElement não pode ser nulo.");
		}

		array.add(element);
	}

	public static void add(JsonObject json, JsonElement element, String name) {
		if (json == null) {
			throw new IllegalArgumentException("Elemento JsonObject não pode ser nulo.");
		} else if (element == null) {
			throw new IllegalArgumentException("Elemento JsonElement não pode ser nulo.");
		} else if (name == null) {
			throw new IllegalArgumentException("Name não pode ser nulo.");
		}

		json.add(name, element);
	}

	public static void addProperty(JsonObject json, String property, String value) {
		if (json == null) {
			throw new IllegalArgumentException("Elemento JsonObject não pode ser nulo.");
		} else if (property == null) {
			throw new IllegalArgumentException("Property não pode ser nulo.");
		} else if (value == null) {
			throw new IllegalArgumentException("Value não pode ser nulo.");
		}

		json.addProperty(property, value);
	}
}