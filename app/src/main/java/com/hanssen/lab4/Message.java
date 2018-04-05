package com.hanssen.lab4;


import java.text.SimpleDateFormat;
import java.util.Date;

class Message {
    String username, message, timestamp;

    Message(String u, String m) {
        username = u; message = m; timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }
}
