package ckb.GitHubManager.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;

public class ImmutablePairDeserializer extends StdDeserializer<ImmutablePair<?, ?>> {

    public ImmutablePairDeserializer() {
        super(ImmutablePair.class);
    }

    @Override
    public ImmutablePair<?, ?> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String first = node.get("path").asText();
        String second = node.get("content").asText();
        return new ImmutablePair<>(first, second);
    }
}
