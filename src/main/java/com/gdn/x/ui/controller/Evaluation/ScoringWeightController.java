/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdn.x.ui.controller.Evaluation;

import com.gdn.x.ui.service.ContentGoldenListService;
import com.gdn.x.ui.service.Evaluation.ParameterWeightService;
import com.gdn.x.ui.service.FileGoldenListService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author rizki.a.utomo
 */
@Controller

@SessionAttributes("var_parameter")
public class ScoringWeightController extends CommonControllerEvaluation {

    @Autowired
    private FileGoldenListService fileGoldenListService;

    @Autowired
    ContentGoldenListService contentGoldenListService;

    @Autowired
    ParameterWeightService parameterWeightService;

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ModelAndView Retrieve(HttpSession session, Model model) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("MapList", getParameter());
        modelAndView.setViewName("evaluation/field-list");
        modelAndView.addObject("listParameterWeight", parameterWeightService.findAll());
        return modelAndView;
    }

    @RequestMapping(value = "/process-retrieve", method = RequestMethod.POST)
    @SuppressWarnings("empty-statement")
    public String processRetrieve(
            @RequestParam(value = "fieldList[]") String[] fieldList,
            @RequestParam(value = "valueList[]") String[] valueList,
            Model model,
            HttpSession session) {

        System.out.println(Arrays.toString(fieldList));
        System.out.println(Arrays.toString(valueList));
        combineFieldListAndValueList(fieldList, valueList);
        return "redirect:/retrieve";
    }

    @RequestMapping(value = {"/run-all-evaluation"}, method = RequestMethod.GET)
    public ModelAndView runAllGoldenListEvaluation(
            @RequestParam(value = "query[]") String[] query,
            @RequestParam(value = "timeStamp") String timeStamp
    ) {

        List<List<String>> listActualId = new ArrayList<List<String>>();
        List<List<String>> listExpectedId = new ArrayList<List<String>>();
        ModelAndView modelAndView = new ModelAndView();
        List<String> RecallCoordinate = new ArrayList<String>();
        Multimap<String, List<Double>> listQueryAndPrecision = ArrayListMultimap.create();
        RecallCoordinate.add("0.0");
        RecallCoordinate.add("0.1");
        RecallCoordinate.add("0.2");
        RecallCoordinate.add("0.3");
        RecallCoordinate.add("0.4");
        RecallCoordinate.add("0.5");
        RecallCoordinate.add("0.6");
        RecallCoordinate.add("0.7");
        RecallCoordinate.add("0.8");
        RecallCoordinate.add("0.9");
        RecallCoordinate.add("1.0");
        int i = 0;
        for (String parameter : query) {
            try {
                listActualId.add(searchByQueryEvaluation(parameter));
            } catch (MalformedURLException ex) {
                Logger.getLogger(ScoringWeightController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SolrServerException ex) {
                Logger.getLogger(ScoringWeightController.class.getName()).log(Level.SEVERE, null, ex);
            }
            listExpectedId.add(contentGoldenListService.findContentGoldenListByQueryAndTimeStamp(parameter, timeStamp).get(0).getExpectedResult());
            listQueryAndPrecision.put(parameter, listInterpolatedPrecision(listExpectedId, listActualId).get(i));
            i++;
        }

        //Method to save parameterWeight To Mongo
//        SaveParameterWeightToMongo(listExpectedId, listActualId);
        modelAndView.addObject("listParameterWeight", parameterWeightService.findAll());
        modelAndView.addObject("MapList", getParameter());
        modelAndView.addObject("precisionCoordinate", interpolateAveragePrecision(listExpectedId, listActualId));
        modelAndView.addObject("recallCoordinate", RecallCoordinate);
        SaveCoordinateToMongo(interpolateAveragePrecision(listExpectedId, listActualId));
        modelAndView.addObject("listQueryAndPrecision", listQueryAndPrecision.asMap());
        modelAndView.addObject("finalAverage", finalScoreInterpolatedPrecision(listExpectedId, listActualId));
        modelAndView.setViewName("evaluation/field-list");
        return modelAndView;

    }

    @RequestMapping(value = {"/executeParameter"}, method = RequestMethod.POST)
    public ModelAndView executeParameter(
            @RequestParam(value = "fieldList[]") String[] fieldList
    ) {
        ModelAndView modelAndView = new ModelAndView();

            // Method to DeleteAll parameterWeight
            parameterWeightService.deleteAll();
        
        
            fetchParameter(fieldList);
            System.out.println("Succes run all query");
            modelAndView.setViewName("Redirect:/retrieve");
            return modelAndView;
        

    }

}
