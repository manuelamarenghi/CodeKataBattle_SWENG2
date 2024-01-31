package ckb.BattleManager.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ImmutablePairDeserializer extends StdDeserializer<ImmutablePair<?, ?>> {

    public ImmutablePairDeserializer() {
        super(ImmutablePair.class);
    }

    @Override
    public ImmutablePair<?, ?> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Assuming there is only one field in the JSON object
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        if (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String first = entry.getKey();        // Get the key
            String second = entry.getValue().asText();  // Get the value as text
            return new ImmutablePair<>(first, second);
        } else {
            throw new IOException("Invalid JSON format: expected a single key-value pair.");
        }
    }
}
