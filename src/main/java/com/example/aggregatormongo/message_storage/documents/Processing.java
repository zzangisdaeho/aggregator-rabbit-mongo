package com.example.aggregatormongo.message_storage.documents;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Date;


@Document(collection = "processing")
@TypeAlias("processing")
@Builder
@Getter
public class Processing {

    @Id
    private String id;

    @Indexed
    private String transactionId;

    private int total;

    private int index;

    private Object data;

    /**
     *  createdAt 값 기준으로 스타팅하여 expireAfter기준점에 도달시 삭제
     *  mongoDB가 내부적으로 document expire를 체크하는 주기가 있어 최대 1분정도 차이날 수 있음
     *  cf) 기준값 바꾸고싶으면 mongodb에서 index 삭제해주고 재실행해야한다. (자동 업데이트 안됨)
     */
    @Builder.Default
    @Indexed(name = "expireAt", expireAfter = "60d")
    private Date createdAt = Date.from(ZonedDateTime.now().toInstant());

}
