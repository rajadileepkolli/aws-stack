package com.learning.awspring.utils;

import com.learning.awspring.web.model.Message;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class FakeObjectCreator {

  public static Message createMessage() {
    Message message = new Message();
    message.setId(RandomStringUtils.randomNumeric(3));
    message.setMessageBody(RandomStringUtils.randomAlphanumeric(100));
    return message;
  }
}
