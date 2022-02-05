package com.skillbox.javapro21.service.kbLayearConverter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class KbLayerConverter {

    public String convertString(String string) {
        StringBuilder resultString = new StringBuilder();
        BiMap<Character, Character> enToRuBiMap = getEnToRuBiMap();
        BiMap<Character, Character> ruToEnBiMap = getRuToEnBiMap();

        resultString.delete(0, resultString.length());
        for (int i = 0; i < string.length(); i++) {
            if (enToRuBiMap.containsKey(string.charAt(i))) {
                resultString.append(enToRuBiMap.get(string.charAt(i)));
            } else if (ruToEnBiMap.containsKey(string.charAt(i))) {
                resultString.append(ruToEnBiMap.get(string.charAt(i)));
            } else {
                resultString.append(string.charAt(i));
            }
        }

        return resultString.toString();
    }

    private BiMap<Character, Character> getEnToRuBiMap() {
        BiMap<Character, Character> enToRusBiMap = HashBiMap.create();
        enToRusBiMap.put('`', 'ё');
        enToRusBiMap.put('q', 'й');
        enToRusBiMap.put('w', 'ц');
        enToRusBiMap.put('e', 'у');
        enToRusBiMap.put('r', 'к');
        enToRusBiMap.put('t', 'е');
        enToRusBiMap.put('y', 'н');
        enToRusBiMap.put('u', 'г');
        enToRusBiMap.put('i', 'ш');
        enToRusBiMap.put('o', 'щ');
        enToRusBiMap.put('p', 'з');
        enToRusBiMap.put('[', 'х');
        enToRusBiMap.put(']', 'ъ');
        enToRusBiMap.put('a', 'ф');
        enToRusBiMap.put('s', 'ы');
        enToRusBiMap.put('d', 'в');
        enToRusBiMap.put('f', 'а');
        enToRusBiMap.put('g', 'п');
        enToRusBiMap.put('h', 'р');
        enToRusBiMap.put('j', 'о');
        enToRusBiMap.put('k', 'л');
        enToRusBiMap.put('l', 'д');
        enToRusBiMap.put(';', 'ж');
        enToRusBiMap.put('\'', 'э');
        enToRusBiMap.put('z', 'я');
        enToRusBiMap.put('x', 'ч');
        enToRusBiMap.put('c', 'с');
        enToRusBiMap.put('v', 'м');
        enToRusBiMap.put('b', 'и');
        enToRusBiMap.put('n', 'т');
        enToRusBiMap.put('m', 'ь');
        enToRusBiMap.put(',', 'б');
        enToRusBiMap.put('.', 'ю');
        enToRusBiMap.put('/', '.');
        enToRusBiMap.put('~', 'Ё');
        enToRusBiMap.put('@', '"');
        enToRusBiMap.put('#', '№');
        enToRusBiMap.put('$', ';');
        enToRusBiMap.put('^', ':');
        enToRusBiMap.put('&', '?');
        enToRusBiMap.put('|', '/');
        enToRusBiMap.put('Q', 'Й');
        enToRusBiMap.put('W', 'Ц');
        enToRusBiMap.put('E', 'У');
        enToRusBiMap.put('R', 'К');
        enToRusBiMap.put('T', 'Е');
        enToRusBiMap.put('Y', 'Н');
        enToRusBiMap.put('U', 'Г');
        enToRusBiMap.put('I', 'Ш');
        enToRusBiMap.put('O', 'Щ');
        enToRusBiMap.put('P', 'З');
        enToRusBiMap.put('{', 'Х');
        enToRusBiMap.put('}', 'Ъ');
        enToRusBiMap.put('A', 'Ф');
        enToRusBiMap.put('S', 'Ы');
        enToRusBiMap.put('D', 'В');
        enToRusBiMap.put('F', 'А');
        enToRusBiMap.put('G', 'П');
        enToRusBiMap.put('H', 'Р');
        enToRusBiMap.put('J', 'О');
        enToRusBiMap.put('K', 'Л');
        enToRusBiMap.put('L', 'Д');
        enToRusBiMap.put(':', 'Ж');
        enToRusBiMap.put('"', 'Э');
        enToRusBiMap.put('Z', 'Я');
        enToRusBiMap.put('X', 'Ч');
        enToRusBiMap.put('C', 'С');
        enToRusBiMap.put('V', 'М');
        enToRusBiMap.put('B', 'И');
        enToRusBiMap.put('N', 'Т');
        enToRusBiMap.put('M', 'Ь');
        enToRusBiMap.put('<', 'Б');
        enToRusBiMap.put('>', 'Ю');
        enToRusBiMap.put('?', ',');
        enToRusBiMap.put(' ', ' ');

        return enToRusBiMap;
    }

    private BiMap<Character, Character> getRuToEnBiMap() {
        return getEnToRuBiMap().inverse();
    }

}
