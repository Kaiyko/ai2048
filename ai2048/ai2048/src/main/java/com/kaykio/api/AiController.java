package com.kaykio.api;

import com.kaykio.action.AiAction;
import com.kaykio.model.GameStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Blue
 */
@RequestMapping("/v1/ai/")
@RestController
public class AiController {

    @CrossOrigin
    @PostMapping("2048/getBestMove")
    public int getBestMove(@RequestParam(value = "cellInfo") List<Integer> cellInfo) {
        int[][] matrix = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = cellInfo.get(4 * i + j);
            }
        }
        GameStatus gird = new GameStatus(matrix);
        AiAction aiAction = new AiAction(gird);
        return aiAction.getBestMove();
    }

}
