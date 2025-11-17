package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.async.ByteBufferFeeder;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/Jackson2Tokenizer.class */
final class Jackson2Tokenizer {
    private final JsonParser parser;
    private final DeserializationContext deserializationContext;
    private final NonBlockingInputFeeder inputFeeder;
    private final boolean tokenizeArrayElements;
    private final boolean forceUseOfBigDecimal;
    private final int maxInMemorySize;
    private int objectDepth;
    private int arrayDepth;
    private int byteCount;
    private TokenBuffer tokenBuffer = createToken();

    private Jackson2Tokenizer(JsonParser parser, DeserializationContext deserializationContext, boolean tokenizeArrayElements, boolean forceUseOfBigDecimal, int maxInMemorySize) {
        this.parser = parser;
        this.deserializationContext = deserializationContext;
        this.inputFeeder = this.parser.getNonBlockingInputFeeder();
        this.tokenizeArrayElements = tokenizeArrayElements;
        this.forceUseOfBigDecimal = forceUseOfBigDecimal;
        this.maxInMemorySize = maxInMemorySize;
    }

    private List<TokenBuffer> tokenize(DataBuffer dataBuffer) {
        try {
            try {
                int bufferSize = dataBuffer.readableByteCount();
                List<TokenBuffer> tokens = new ArrayList<>();
                NonBlockingInputFeeder nonBlockingInputFeeder = this.inputFeeder;
                if (nonBlockingInputFeeder instanceof ByteBufferFeeder) {
                    ByteBufferFeeder byteBufferFeeder = (ByteBufferFeeder) nonBlockingInputFeeder;
                    DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers();
                    while (iterator.hasNext()) {
                        try {
                            byteBufferFeeder.feedInput(iterator.next());
                            parseTokens(tokens);
                        } catch (Throwable th) {
                            if (iterator != null) {
                                try {
                                    iterator.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    }
                    if (iterator != null) {
                        iterator.close();
                    }
                } else {
                    NonBlockingInputFeeder nonBlockingInputFeeder2 = this.inputFeeder;
                    if (nonBlockingInputFeeder2 instanceof ByteArrayFeeder) {
                        ByteArrayFeeder byteArrayFeeder = (ByteArrayFeeder) nonBlockingInputFeeder2;
                        byte[] bytes = new byte[bufferSize];
                        dataBuffer.read(bytes);
                        byteArrayFeeder.feedInput(bytes, 0, bufferSize);
                        parseTokens(tokens);
                    }
                }
                assertInMemorySize(bufferSize, tokens);
                DataBufferUtils.release(dataBuffer);
                return tokens;
            } catch (JsonProcessingException ex) {
                throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex);
            } catch (IOException ex2) {
                throw Exceptions.propagate(ex2);
            }
        } catch (Throwable th3) {
            DataBufferUtils.release(dataBuffer);
            throw th3;
        }
    }

    private Flux<TokenBuffer> endOfInput() {
        return Flux.defer(() -> {
            this.inputFeeder.endOfInput();
            try {
                List<TokenBuffer> tokens = new ArrayList<>();
                parseTokens(tokens);
                return Flux.fromIterable(tokens);
            } catch (JsonProcessingException ex) {
                throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex);
            } catch (IOException ex2) {
                throw Exceptions.propagate(ex2);
            }
        });
    }

    private void parseTokens(List<TokenBuffer> tokens) throws IOException {
        JsonToken token;
        boolean previousNull = false;
        while (!this.parser.isClosed() && (token = this.parser.nextToken()) != JsonToken.NOT_AVAILABLE) {
            if (token != null || !previousNull) {
                if (token == null) {
                    previousNull = true;
                } else {
                    previousNull = false;
                    updateDepth(token);
                    if (!this.tokenizeArrayElements) {
                        processTokenNormal(token, tokens);
                    } else {
                        processTokenArray(token, tokens);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void updateDepth(JsonToken token) {
        switch (token) {
            case START_OBJECT:
                this.objectDepth++;
                return;
            case END_OBJECT:
                this.objectDepth--;
                return;
            case START_ARRAY:
                this.arrayDepth++;
                return;
            case END_ARRAY:
                this.arrayDepth--;
                return;
            default:
                return;
        }
    }

    private void processTokenNormal(JsonToken token, List<TokenBuffer> result) throws IOException {
        this.tokenBuffer.copyCurrentEvent(this.parser);
        if ((token.isStructEnd() || token.isScalarValue()) && this.objectDepth == 0 && this.arrayDepth == 0) {
            result.add(this.tokenBuffer);
            this.tokenBuffer = createToken();
        }
    }

    private void processTokenArray(JsonToken token, List<TokenBuffer> result) throws IOException {
        if (!isTopLevelArrayToken(token)) {
            this.tokenBuffer.copyCurrentEvent(this.parser);
        }
        if (this.objectDepth == 0) {
            if (this.arrayDepth == 0 || this.arrayDepth == 1) {
                if (token == JsonToken.END_OBJECT || token.isScalarValue()) {
                    result.add(this.tokenBuffer);
                    this.tokenBuffer = createToken();
                }
            }
        }
    }

    private TokenBuffer createToken() {
        TokenBuffer tokenBuffer = new TokenBuffer(this.parser, this.deserializationContext);
        tokenBuffer.forceUseOfBigDecimal(this.forceUseOfBigDecimal);
        return tokenBuffer;
    }

    private boolean isTopLevelArrayToken(JsonToken token) {
        return this.objectDepth == 0 && ((token == JsonToken.START_ARRAY && this.arrayDepth == 1) || (token == JsonToken.END_ARRAY && this.arrayDepth == 0));
    }

    private void assertInMemorySize(int currentBufferSize, List<TokenBuffer> result) {
        if (this.maxInMemorySize >= 0) {
            if (!result.isEmpty()) {
                this.byteCount = 0;
                return;
            }
            if (currentBufferSize > Integer.MAX_VALUE - this.byteCount) {
                raiseLimitException();
                return;
            }
            this.byteCount += currentBufferSize;
            if (this.byteCount > this.maxInMemorySize) {
                raiseLimitException();
            }
        }
    }

    private void raiseLimitException() {
        throw new DataBufferLimitException("Exceeded limit on max bytes per JSON object: " + this.maxInMemorySize);
    }

    public static Flux<TokenBuffer> tokenize(Flux<DataBuffer> dataBuffers, JsonFactory jsonFactory, ObjectMapper objectMapper, boolean tokenizeArrays, boolean forceUseOfBigDecimal, int maxInMemorySize) {
        JsonParser parser;
        try {
            if (jsonFactory.getFormatName().equals("Smile")) {
                parser = jsonFactory.createNonBlockingByteArrayParser();
            } else {
                parser = jsonFactory.createNonBlockingByteBufferParser();
            }
            DeserializationContext context = objectMapper.getDeserializationContext();
            if (context instanceof DefaultDeserializationContext) {
                DefaultDeserializationContext ddc = (DefaultDeserializationContext) context;
                context = ddc.createInstance(objectMapper.getDeserializationConfig(), parser, objectMapper.getInjectableValues());
            }
            Jackson2Tokenizer tokenizer = new Jackson2Tokenizer(parser, context, tokenizeArrays, forceUseOfBigDecimal, maxInMemorySize);
            Objects.requireNonNull(tokenizer);
            return dataBuffers.concatMapIterable(tokenizer::tokenize).concatWith(tokenizer.endOfInput());
        } catch (IOException ex) {
            return Flux.error(ex);
        }
    }
}
