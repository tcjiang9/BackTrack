package io.intrepid.nostalgia.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.intrepid.nostalgia.models.nytmodels.Byline;
import io.intrepid.nostalgia.models.nytmodels.Doc;
import io.intrepid.nostalgia.services.NytService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class NytServiceAdapter {

    private static DocDeserializer docDeserializer = new DocDeserializer();
    private static final Gson defaultGson = new GsonBuilder().create();

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Doc.class, docDeserializer).create();
    private static final String NYT_ENDPOINT = "http://api.nytimes.com";
    private static RestAdapter serviceAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(NYT_ENDPOINT)
            .setConverter(new GsonConverter(GSON))
            .build();
    private static NytService nytServiceInstance;

    public static NytService getNytServiceInstance() {
        if (nytServiceInstance == null) {
            nytServiceInstance = serviceAdapter.create(NytService.class);
        }
        return nytServiceInstance;
    }

    public static class DocDeserializer implements JsonDeserializer<Doc> {

        @Override
        public Doc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject docObject = json.getAsJsonObject();
            boolean hasCorrectDataType = docObject.get("byline").isJsonObject();
            if (!hasCorrectDataType) {
                docObject.remove("byline");
                docObject.add("byline", new JsonObject());
            }
            return defaultGson.fromJson(docObject.toString(), typeOfT);
        }
    }

}
