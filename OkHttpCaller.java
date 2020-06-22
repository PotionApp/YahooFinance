import com.google.gson.*;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import netscape.javascript.JSObject;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.IOException;

import java.util.*;

public class OkHttpCaller {

    public static void main(String[] args)
    {
        JsonElement jsonTree = getDataAsJson(Provider.API_DOJO, Constants.RAPID_API_KEY, Stock.MODERNA);
        DatabaseMap map = new DatabaseMap(jsonTree);


//        System.out.println(map.getSQLcommand(Constants.STOCK_SUMMARY_TABLE, SQLcommand.CREATE));
        System.out.println(map.getSQLcommand(Constants.STOCK_SUMMARY_TABLE, SQLcommand.INSERT));
    }

    public static class DatabaseMap
    {
        HashMap<String, JsonElement> map = null;
        List<String> keys = null;
        List<JsonElement> values = null;

        DatabaseMap()
        {
            map = new HashMap<String, JsonElement>();
            keys = new ArrayList<String>();
            values = new ArrayList<JsonElement>();
        }

        DatabaseMap(JsonElement root)
        {
            this();
            generateMap(root, "", map);
            for(Map.Entry<String, JsonElement> entry : map.entrySet())
            {
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
        }

        public String getSQLcommand(String table, SQLcommand type)
        {
            String command = type.name + Constants.SPACE + table + Constants.SPACE;

            switch(type)
            {
                case CREATE:
                    command = command + Constants.BRACKET_LEFT;
                    for(String key: keys)
                    {
                        command = command + key + Constants.SPACE + "VARCHAR(10)" + Constants.COMMA + Constants.SPACE;
                    }
                    command = command.substring(0, command.length() - 1);
                    break;
                case INSERT:
                    command = command + Constants.VALUES + Constants.SPACE + Constants.BRACKET_LEFT;
                    for(JsonElement value: values)
                    {
                        command = command + Constants.QUOTE_SINGLE + removeDoubleQuotes(value.toString()) + Constants.QUOTE_SINGLE + Constants.COMMA;
                    }
                    break;
                default:
                    return command;
            }
            command = command.substring(0, command.length() - 1) + Constants.BRACKET_RIGHT + Constants.SEMI_COLON;
            return command;
        }

        public String removeDoubleQuotes(String string)
        {
            if(string.charAt(0) == Constants.QUOTES_DOUBLE.charAt(0) && string.charAt(string.length()-1) == Constants.QUOTES_DOUBLE.charAt(0))
            {
                return string.substring(1,string.length() - 1);
            }
            else
            {
                return string;
            }
        }
    }

    public enum SQLcommand
    {
        CREATE(Constants.CREATE + Constants.SPACE + Constants.TABLE),
        INSERT(Constants.INSERT + Constants.SPACE + Constants.INTO),
        SELECT(Constants.SELECT);

        public String name;

        SQLcommand(String name)
        {
            this.name = name;
        }
    }

    public static void printTrie(NiceTrie root)
    {
        if(root.children == null)
        {
            return;
        }
        else
        {
            Set<String> keys = root.getAllKeys();
            System.out.println(keys);
            Iterator<String> iter = keys.iterator();
            NiceTrie next = new NiceTrie();
            while(iter.hasNext())
            {
                String nextKey = iter.next();
                System.out.println("Next Key: " + nextKey);
                next = root.children.get(nextKey);
                printTrie(next);
            }
        }
    }

    public static void printJsonTree(JsonElement root, String key)
    {
        if(root.isJsonNull())
        {
            return;
        }
        else if(root.isJsonPrimitive())
        {
            System.out.println("Value: " + root);
        }
        else if (root.isJsonObject())
        {
            JsonObject object = root.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries)
            {
                String newKey = key + "." + entry.getKey();
                if (entry.getValue().isJsonPrimitive())
                {
                    System.out.println("NewKey: " + newKey);
                }
                printJsonTree(entry.getValue(), newKey);
            }
        }
        else
        {
            System.out.println("Key: " + key);
            System.out.println(root.getAsJsonArray());
        }
    }

    public static class Constants
    {
        public static String SPACE = " ";
        public static String STAR = "*";
        public static String CREATE = "CREATE";
        public static String INSERT = "INSERT";
        public static String SELECT = "SELECT";
        public static String TABLE = "TABLE";
        public static String VALUES = "VALUES";
        public static String FROM = "FROM";
        public static String INTO = "INTO";
        public static String WHERE = "WHERE";
        public static String BRACKET_LEFT = "(";
        public static String BRACKET_RIGHT = ")";
        public static String BRACKET_LEFT_CURLY = "{";
        public static String BRACKET_RIGHT_CURLY = "}";
        public static String COMMA = ",";
        public static String SEMI_COLON = ";";
        public static String COLON = ":";
        public static String QUOTES_DOUBLE = "\"";
        public static String QUOTE_SINGLE = "'";

        public static String STOCK = "stock";
        public static String STOCK_SUMMARY_TABLE = "STOCK_SUMMARY_TABLE";
        public static String API_DOJO_BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/";
        public static String YAHOO_FINANCE_BASE_URL = "https://yahoo-finance15.p.rapidapi.com/";
        public static String API_DOJO_RAPID_API_HOST = "apidojo-yahoo-finance-v1.p.rapidapi.com";
        public static String YAHOO_FINANCE_RAPID_API_HOST = "yahoo-finance15.p.rapidapi.com";
        public static String RAPID_API_HOST_IDENTIFIER = "x-rapidapi-host";
        public static String RAPID_API_KEY_IDENTIFIER = "x-rapidapi-key";

        public static String RAPID_API_KEY = "635b42af5emshb39964800077642p12a021jsndaeeabe69322";
    }



    public enum Provider
    {
        API_DOJO(Constants.API_DOJO_BASE_URL, Constants.API_DOJO_RAPID_API_HOST),
        YAHOO_FINANCE(Constants.YAHOO_FINANCE_BASE_URL, Constants.YAHOO_FINANCE_RAPID_API_HOST);

        public String baseURL;
        public String rapidAPIhost;

        Provider(String baseURL, String rapidAPIhost)
        {
            this.baseURL = baseURL;
            this.rapidAPIhost = rapidAPIhost;
        }

        public String getAPIurl(Stock stock)
        {
            return baseURL + "stock/v2/get-summary?region=US&symbol=" + stock.symbol;
        }
    }

    public enum API
    {
        STOCK_NEWS,
        STOCK_SUMMARY,
        STOCK_HOLDERS,
        STOCK_FINANCIALS,
        STOCK_OPTIONS,
        STOCK_ANALYSIS;
    }

    public enum Region
    {
        US("US", "United States of America"),
        EU("EU", "European Union"),
        DE("DE", "Germany"),
        UK("UK", "United Kingdom");

        public String id;
        public String name;

        Region(String id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    public enum Stock
    {
        MODERNA("MRNA", "Moderna"),
        MICROSOFT("MSFT", "Microsoft"),
        APPLE("AAPL", "Apple");

        public String symbol;
        public String name;

        Stock(String symbol, String name)
        {
            this.symbol = symbol;
            this.name = name;
        }
    }

    public static JsonElement getDataAsJson(Provider provider, String apiKey, Stock stock)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(provider.getAPIurl(stock))
                .get()
                .addHeader(Constants.RAPID_API_HOST_IDENTIFIER, provider.rapidAPIhost)
                .addHeader(Constants.RAPID_API_KEY_IDENTIFIER, apiKey)
                .build();

        JsonElement jsonTree = null;

        try
        {
            ResponseBody responseBody = client.newCall(request).execute().body();
            String json = responseBody.string();
            json = json.substring(0, json.length() - 1) + Constants.COMMA +
                        Constants.QUOTES_DOUBLE + Constants.STOCK + Constants.QUOTES_DOUBLE +
                        Constants.COLON + Constants.QUOTES_DOUBLE + stock.symbol + Constants.QUOTES_DOUBLE +
                        Constants.BRACKET_RIGHT_CURLY;
//            System.out.println(json);

            Gson gson = new Gson();

            JsonParser parser = new JsonParser();
            jsonTree = parser.parse(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonTree;
    }

    public static void generateMap(JsonElement root, String key, HashMap<String, JsonElement> map)
    {
        if(root.isJsonNull())
        {
            return;
        }

        if(root.isJsonPrimitive())
        {
            map.put(key, root);
            return;
        }
        else if (root.isJsonObject())
        {
            JsonObject object = root.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries)
            {
                String newKey = key + "." + entry.getKey();
                if (entry.getValue().isJsonPrimitive())
                {
                    map.put(newKey, entry.getValue());
                }
                generateMap(entry.getValue(), newKey, map);
            }
        }
        else
        {
            map.put(key, root);
            return;
        }
    }
}
