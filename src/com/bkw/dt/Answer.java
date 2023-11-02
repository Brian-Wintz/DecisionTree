package com.bkw.dt;

/**
 * Copyright (C) 2023 Brian Wintz.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */
public class Answer {
    private String key;
    private String text;
    private String nextQuestionKey;
    private String lastQuestionKey;

    public Answer(String key,String text,String nextQuestionKey) {
        this.key=key;
        this.text=text;
        this.nextQuestionKey=nextQuestionKey;
        this.lastQuestionKey=null;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getNextQuestionKey() {
        return nextQuestionKey;
    }

    public String getLastQuestionKey() {
        return lastQuestionKey;
    }

    public void setLastQuestionKey(String lastQuestionKey) {
        this.lastQuestionKey=lastQuestionKey;
    }

    public String toJSONString() {
        String result="      {";
        result+="\"key\": \""+this.key+"\"";
        result+=", \"text\": \""+this.text+"\"";
        if(this.nextQuestionKey!=null)
            result+=", \"nextQuestionKey\": \""+this.nextQuestionKey+"\"";
        if(this.lastQuestionKey!=null)
            result+=", \"lastQuestionKey\": \""+this.lastQuestionKey+"\"";
        result+="}";
        return result;
    }

    @Override
    public String toString() {
        return key+":"+text+":"+(nextQuestionKey==null?"<null>":nextQuestionKey+":"+(lastQuestionKey==null?"<null>":lastQuestionKey));
    }
}
