package com.thefreak.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        newImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        return newImage;
    }

    public static Integer[][] levelParser(String fileName) {
        Integer[][] result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            List<Integer[]> lvlLines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                lvlLines.add(strToIntArr(tokens).toArray(new Integer[0]));
            }
            result = new Integer[lvlLines.size()][lvlLines.get(0).length];
            for (int i = 0; i < lvlLines.size(); i++)
                result[i] = lvlLines.get(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<Integer> strToIntArr(String[] strArr) {
        return Arrays.stream(strArr).map(Integer::valueOf).collect(Collectors.toList());
    }
}
