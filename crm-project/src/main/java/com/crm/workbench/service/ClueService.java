package com.crm.workbench.service;

import com.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {


    /**
     * 查询所有的线索
     * @return
     */
    List<Clue> queryAllClues();

    /**
     * 保存创建线索
     * @param clue
     * @return
     */
    int saveCreateClue(Clue clue);

    /**
     *
     * @param id
     * @return
     */
    Clue queryClueForDetailById(String id);

    void saveConvertClue(Map<String,Object> map);
}
