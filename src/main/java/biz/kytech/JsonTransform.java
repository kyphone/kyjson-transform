package biz.kytech;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JSON transformation class
 */
public class JsonTransform {

    /**
     * Transform a JSON with template
     *
     * @param json Original JSON
     * @param tmpl Template
     * @return Transformed JSON
     * @throws Exception
     */
    public static String transform(String json, String tmpl) throws Exception {
        ReadContext ctx = JsonPath.parse(json);

        tmpl = tmpl.trim();

        if (tmpl.startsWith("{") && tmpl.endsWith("}")) {
            Object o = transform(ctx, null, new JSONObject(tmpl));
            if (o instanceof JSONObject) {
                return ((JSONObject) o).toString(2);
            } else if (o instanceof JSONArray) {
                return ((JSONArray) o).toString(2);
            }
        } else if (tmpl.startsWith("[") && tmpl.endsWith("]")) {
            return transform(ctx, null, new JSONArray(tmpl)).toString(2);
        }

        return "";
    }

    private static JSONArray transform(ReadContext ctx, ReadContext thisCtx, JSONArray tmpl) throws Exception {
        JSONArray r = new JSONArray();

        for (int i = 0; i < tmpl.length(); i++) {
            r.put(jsonPathValue(ctx, thisCtx, tmpl.get(i)));
        }

        return r;
    }

    private static Object transform(ReadContext ctx, ReadContext thisCtx, JSONObject tmpl) throws Exception {

        JSONObject r = new JSONObject();

        Iterator i = tmpl.keys();
        while (i.hasNext()) {
            String k = (String) i.next();
            Object o = tmpl.get(k);

            if (k.startsWith("#foreach(")) {
                String path = k.substring(9, k.length() - 1);
                List<Object> objects = (List<Object>) jsonPathValue(ctx, thisCtx, path);
                JSONArray r2 = new JSONArray();
                for (Object object : objects) {
                    ReadContext ctx2 = JsonPath.parse(object);
                    Object o2 = transform(ctx, ctx2, (JSONObject) o);
                    r2.put(o2);
                }
                return r2;
            }

            r.put(k, jsonPathValue(ctx, thisCtx, o));
        }

        return r;
    }

    private static Object jsonPathValue(ReadContext ctx, ReadContext thisCtx, Object oPath) throws Exception {
        if (oPath instanceof JSONObject) {
            return transform(ctx, thisCtx, (JSONObject) oPath);

        } else if (oPath instanceof JSONArray) {
            return transform(ctx, thisCtx, (JSONArray) oPath);

        } else if (oPath instanceof String && ((String) oPath).startsWith("$.")) {
            Object v;
            try {
                String path = (String) oPath;
                if (path.startsWith("$.this.") && thisCtx != null) {
                    path = path.replace("$.this.", "$.");
                    v = thisCtx.read(path);
                } else if (ctx != null) {
                    v = ctx.read(path);
                } else {
                    return "";
                }
            } catch (Exception ex) {
                return "";
            }
            if (v instanceof List) {
                List array = (List) v;
                List<Object> values = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    values.add(jsonPathValue(ctx, thisCtx, array.get(i)));
                }
                return values;

            } else if (v instanceof String) {
                return (String) v;
            } else if (v instanceof Integer) {
                return (Integer) v;
            } else if (v instanceof Double) {
                return (Double) v;
            }

        } else if (oPath instanceof String && ((String) oPath).startsWith("#str(")) {
            String path = ((String) oPath).substring(5, ((String) oPath).length() - 1);
            return String.valueOf(jsonPathValue(ctx, thisCtx, path));

        } else if (oPath instanceof String && ((String) oPath).startsWith("#num(")) {
            String path = ((String) oPath).substring(5, ((String) oPath).length() - 1);
            return Double.parseDouble(String.valueOf(jsonPathValue(ctx, thisCtx, path)));

        }

        return oPath;
    }
}
