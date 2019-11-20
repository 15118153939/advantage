package com.lv.advantage.vo;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 13:49
 * @Description:
 */
public class MyMessage {
    private MyHeader myHeader;
    private Object body;

    public MyHeader getMyHeader() {
        return myHeader;
    }

    public void setMyHeader(MyHeader myHeader) {
        this.myHeader = myHeader;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "myHeader=" + myHeader +
                ", body=" + body +
                '}';
    }
}
