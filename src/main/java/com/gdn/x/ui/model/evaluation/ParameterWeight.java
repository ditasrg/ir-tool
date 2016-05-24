/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdn.x.ui.model.evaluation;

import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author alumunia
 */
@Document(collection = "parameterWeight")
public class ParameterWeight {

    private String timeStamp;

    private Map<String, String> weightParameter;

    private String finalScore;

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }
    
    

    public Map<String, String> getWeightParameter() {
        return weightParameter;
    }

    public void setWeightParameter(Map<String, String> weightParameter) {
        this.weightParameter = weightParameter;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
