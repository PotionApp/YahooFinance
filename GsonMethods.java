import com.google.gson.JsonElement;

public class GsonMethods
{
    public static JsonElement getJsonElementForKey(String key, JsonElement element)
    {
        if(element.isJsonObject())
        {
            return element.getAsJsonObject().get(key);
        }
        else
        {
            return null;
        }
    }

    public static JsonElement getJsonElement(String[] keyMap, int index, JsonElement element)
    {
        if (index >= keyMap.length - 1)
        {
            return getJsonElementForKey(keyMap[index], element);
        }
        else
        {
            return getJsonElement(keyMap, index + 1, getJsonElementForKey(keyMap[index], element));
        }
    }
}
