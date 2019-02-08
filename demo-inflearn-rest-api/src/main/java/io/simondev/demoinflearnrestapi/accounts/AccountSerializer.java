package io.simondev.demoinflearnrestapi.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

// @JsonComponent를 넣으면 objectMapper에 등록이 되고,
// Account를 내보낼 때마다, id만 나가게 된다.
// 하지만, id 외에 정보 수정이나 열람이 필요할 수 있으므로
// 여기서는 등록하지 않겠다.
public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account account, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", account.getId());
        gen.writeEndObject();
    }
}
