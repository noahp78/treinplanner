package me.noahp78.travel;

import java.time.LocalDateTime;


/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class NSReis {
    LocalDateTime  vertrek;
    LocalDateTime aankomst;

    public NSReis(LocalDateTime vertrek, LocalDateTime aankomst) {
        this.vertrek = vertrek;
        this.aankomst = aankomst;
    }
}
