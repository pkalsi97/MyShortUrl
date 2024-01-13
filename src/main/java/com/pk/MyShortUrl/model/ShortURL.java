package com.pk.MyShortUrl.model;
// for reduction of boilerplate code.
import lombok.Data;
import lombok.NoArgsConstructor;
// annotating fields to the database
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
// to fetch th date and time
import java.time.LocalDateTime;


// data will be stored in the collection "shorturls". if not present Mongodb will automatically
// generate this collection.
@Document(collection = "shorturls")
@Data
@NoArgsConstructor
public class ShortURL {

    // fields required to be stored in the database
    @Id
    private String id;
    private String originalUrl;
    private String shortLink;
    private String qrCode;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private boolean active;
    private String userId;
    private int clickCount;

}
