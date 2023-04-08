package org.wgcisotto.request.logger.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class CustomDateTimeSerializer extends StdSerializer<DateTime> {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public CustomDateTimeSerializer(Class<DateTime> t) {
        super(t);
    }

    public CustomDateTimeSerializer() {
        this(null);
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        String date = simpleDateFormat.format(dateTime.toDate());
        jsonGenerator.writeString(date);
    }
}
