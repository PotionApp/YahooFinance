import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NiceTrie
{
    HashMap<String, NiceTrie> children = new HashMap<String, NiceTrie>();

    public  NiceTrie() {}

    public static NiceTrie getTrieFrom(JsonElement element)
    {
        NiceTrie trie = new NiceTrie();
        if(element == null)
        {
            return trie;
        }
        if(element.isJsonNull() || element.isJsonPrimitive())
        {
            return trie;
        }
        else if(element.isJsonArray())
        {
            JsonArray array = element.getAsJsonArray();
            for(Integer i = 0; i < array.size(); i++)
            {
                trie.children.put(i.toString(), getTrieFrom(array.get(i)));
            }
        }
        else
        {
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries)
            {
                trie.children.put(entry.getKey(), getTrieFrom(object.get(entry.getKey())));
            }
        }
        return trie;
    }

    public void printAllChildren()
    {
        if(this.children == null)
        {
            return;
        }
        else
        {
            for(HashMap.Entry<String, NiceTrie> child: this.children.entrySet())
            {
                System.out.println("Key: " + child.getKey() + ", Value: " + child.getValue());
            }
        }
    }

    public Set<String> getAllKeys()
    {
        Set<String> keys = new HashSet<String>();
        if(this.children == null)
        {
            return keys;
        }
        else
        {
            for(HashMap.Entry<String, NiceTrie> child: this.children.entrySet())
            {
                keys.add(child.getKey());
            }
        }
        return keys;
    }
}
