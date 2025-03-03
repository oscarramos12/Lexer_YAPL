import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Stack;


public class arbolLex {
    public static void readYal(){
        String dir = "C:\\Users\\Oscar\\Desktop\\Lab A\\slr-4.yal";
        HashMap<String, String> ptResult = processToken(processLet(dir), dir);
        String postfix = ptResult.get("-1");
        ptResult.remove("-1");
        System.out.println("PT RESUTL: " + ptResult);
        createTree create = new createTree('@', ptResult);
        treeNode root = create.buildTree(postfix);
        pruebas.graficardd(root);
    }

    public static ArrayList<HashMap<String,String>> processLet(String dir){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dir)); 
            HashMap<String,String> conjOperaciones = new HashMap<String, String>();
            HashMap<String, String> simpleIDs = new HashMap<String, String>();
            HashMap<String, String> reverse = new HashMap<String, String>();
            String abc = "a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z";
            String ABC = "A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z";

            String numeros = "0|1|2|3|4|5|6|7|8|9";
            String line;
            String key;
            int keyInt = 100;
            boolean hasKey = false;
            ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
                while ((line = reader.readLine()) != null) {
                    hasKey = false;
                    if(line.contains("let")){
                        //agarramos la key
                        for (int i = 0; i < line.length(); i++){
                            key  = "";
                            if(line.charAt(i) == ' ' && !hasKey){
                                i += 1;
                                for (int getKey = i; line.charAt(getKey) != ' '; getKey++ ){
                                    key += line.charAt(getKey);
                                    i += 1;
                                }
                                hasKey = true;
                                //System.out.println("KEY: " + key);
                                //System.out.println(line.charAt(i+1) + "|" +line.charAt(i+4));
                                //si es un conjunto sin operaciones
                                String[] separacion = line.split("=");
                                String value = separacion[1].replace("' '", "'empty'");
                                value = value.replace(" ", "");
                                //System.out.println("VALUE: "  + value);
                                if(value.charAt(0) == '[' && value.charAt(1) == '\'' && value.charAt(value.length()-1) == ']' && value.charAt(value.length()-2) == '\''){
                                    int startIndex = value.indexOf('[') + 1;
                                    int endIndex = value.indexOf(']');
                                    String conjunto = value.substring(startIndex, endIndex);
                                    conjunto = conjunto.replace("''", "|");
                                    conjunto = conjunto.replace("'", "");
                                    if(conjunto.equals("A-Z|a-z")){
                                        conjOperaciones.put(key, ABC+"|"+abc);
                                        simpleIDs.put(Integer.toString(keyInt), key);
                                        reverse.put(key, Integer.toString(keyInt));
                                        keyInt++;
                                    }
                                    else if(conjunto.equals("0-9")){
                                        conjOperaciones.put(key, numeros);
                                        simpleIDs.put(Integer.toString(keyInt), key);
                                        reverse.put(key, Integer.toString(keyInt));
                                        keyInt++;
                                    }
                                    else if(conjunto.equals("A-Z")){
                                        conjOperaciones.put(key, ABC);
                                        simpleIDs.put(Integer.toString(keyInt), key);
                                        reverse.put(key, Integer.toString(keyInt));
                                        keyInt++;
                                    }
                                    else if(conjunto.equals("a-z")){
                                        conjOperaciones.put(key, abc);
                                        simpleIDs.put(Integer.toString(keyInt), key);
                                        reverse.put(key, Integer.toString(keyInt));
                                        keyInt++;
                                    }
                                    else{
                                        conjOperaciones.put(key, conjunto);
                                        simpleIDs.put(Integer.toString(keyInt), key);
                                        reverse.put(key, Integer.toString(keyInt));
                                        keyInt++;
                                    }
                                }
                                else if(value.charAt(0) == '[' && value.charAt(1) == '\"' && value.charAt(value.length()-1) == ']' && value.charAt(value.length()-2) == '\"'){
                                    String justValue = value.substring(2,value.length()-2);
                                    String formateado = "";
                                    for (int addSep = 0; addSep < justValue.length(); addSep++){
                                        if(addSep == justValue.length()-1){
                                            formateado += justValue.charAt(addSep);
                                        }
                                        else{
                                            formateado += justValue.charAt(addSep) + "|";
                                        }
                                    }
                                    conjOperaciones.put(key, formateado);
                                    simpleIDs.put(Integer.toString(keyInt), key);
                                    reverse.put(key, Integer.toString(keyInt));
                                    keyInt++;
                                }
                                else{
                                    for (int x = 0; x < value.length(); x++){
                                        if(Character.isLetter(value.charAt(x))){
                                            String operador = "";
                                            for (int j = x; j < value.length(); j++){
                                                if(Character.isLetter(value.charAt(j))){
                                                    operador += value.charAt(j);
                                                }
                                                else{
                                                    if(!simpleIDs.containsValue(operador)){
                                                            simpleIDs.put(Integer.toString(keyInt), operador);
                                                            reverse.put(operador, Integer.toString(keyInt));
                                                            value = value.substring(0, x) + Integer.toString(keyInt) + value.substring(j, value.length());
                                                            keyInt++;
                                                            x = j - operador.length();
                                                            break;     
                                                    }
                                                    else if(value.length() == 2 && (value.charAt(1) == '+' || value.charAt(1) == '?')){
                                                        value = "(" + value.charAt(0) + ")" + value.charAt(1);
                                                    }
                                                    else{
                                                        value = value.substring(0, x) + reverse.get(operador) + value.substring(j, value.length());
                                                        x = j - operador.length();
                                                        operador = "";
                                                        break;
                                                    }
                                                }
                                            }
                                            
                                        }
                                    }
                                    if(value.contains("'+'")){
                                        value = value.replace("'+'", "'" + Integer.toString(keyInt) + "'");
                                        simpleIDs.put(Integer.toString(keyInt), "+");
                                        keyInt++;
                                    }
                                    if(value.contains("'?'")){
                                        value = value.replace("'?'", "'" + Integer.toString(keyInt) + "'");
                                        simpleIDs.put(Integer.toString(keyInt), "?");
                                        keyInt++;
                                    }

                                    //System.out.println("ANSTES: " + value);
                                    for(int k = 0; k < value.length(); k++){
                                        if(value.charAt(k) == '[' && (value.charAt(k + 1) == '\'')){
                                            String newInsert = "(";
                                            int end = 0;
                                            for(int h = k+1; h < value.length(); h++){
                                                if(value.charAt(h) != ']'){
                                                    newInsert += value.charAt(h);
                                                }
                                                else if(value.charAt(h) == ']'){
                                                    newInsert += ")";
                                                    end = h;
                                                    break;
                                                }
                                            }
                                            newInsert = newInsert.replace("''", "|");
                                            newInsert = newInsert.replace("\'", "");
                                            value = value.substring(0, k) + newInsert + value.substring(end+1, value.length());
                                            k = 0;
                                        }
                                        else if(value.charAt(k) == '\'' && Character.isLetter(value.charAt(k+1)) && value.charAt(k+2) == '\''){
                                            k = k+2;
                                        }
                                        else if(value.charAt(k) == '\'' && Character.isLetter(value.charAt(k + 3))){
                                            value = value.substring(0, k) + value.charAt(k+1) + "|" + value.substring(k+3, value.length());
                                            k = 0;
                                        }
                                        
                                    }
                                    /*value = value.replace("[", "(");
                                    value = value.replace("]", ")");*/
                                    value = value.replace("\'", "");
                                    if(value.length() == 2 && (value.charAt(1) == '?' || value.charAt(1) == '+')){
                                        value = "(" + value.charAt(0) + ")" + value.charAt(1);
                                    }
                                    //System.out.println("Cambio comillas: " + value);
                                    value = NewcambioMas(value);
                                    value = NewcambioInterrogacion(value);
                                    conjOperaciones.put(key, value);
                                    //System.out.println(value);
                                }
                            }                        
                        }
                    }
                }
                reader.close();
                //System.out.println("HashMap:");
                //System.out.println(simpleIDs);
                //System.out.println(conjOperaciones);
                result.add(conjOperaciones);
                result.add(simpleIDs);
                return result;
            }catch (IOException e){
                System.err.println("Error reading file: " + e.getMessage());
                return null;
            }
    }   

    public static HashMap<String, String> processToken(ArrayList<HashMap<String,String>> results, String dir ){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dir));
            String line;
            HashMap<String, String> conjOperaciones = results.get(0);
            HashMap<String, String> simpleIDs = results.get(1);
            HashMap<String, String> newIDs = new HashMap<String, String>();
            int newIDsInt = 10000000;
            String bigExpression = "";
            while ((line = reader.readLine()) != null) {
                if(line.contains("rule")){
                    while ((line = reader.readLine()) != null) {
                        //line = reader.readLine();
                        if(!line.isEmpty()){
                            if(line.contains("return")){
                                String token = "";
                                for (int findChar = 0; findChar < line.length(); findChar++){
                                    if(line.charAt(findChar) != ' ' && line.charAt(findChar) != '{' && line.charAt(findChar) != '|' && line.charAt(findChar) != '\'' && line.charAt(findChar) != '\"'){
                                        token += line.charAt(findChar);
                                    }
                                    else if(line.charAt(findChar) == '{'){
                                        break;
                                    }
                                }
                                if(conjOperaciones.containsKey(token)){
                                    int inicio = line.indexOf("{");
                                    int fin = line.indexOf("}");
                                    String dummy = line.substring(inicio+1, fin);
                                    dummy = dummy.replace(" ", "");
                                    dummy = dummy.replace("return", "");
                                    bigExpression += "((" + conjOperaciones.get(token) + ")" + dummy + ")|";
                                }
                                else{
                                    newIDs.put(Integer.toString(newIDsInt), token);
                                    int inicio = line.indexOf("{");
                                    int fin = line.indexOf("}");
                                    String dummy = line.substring(inicio+1, fin);
                                    dummy = dummy.replace(" ", "");
                                    dummy = dummy.replace("return", "");
                                    bigExpression += "((" + newIDsInt + ")" + dummy + ")|";
                                    newIDsInt++;
                                }
                            }
                            else{
                                String token = "";
                            for (int findChar = 0; findChar < line.length(); findChar++){
                                if(line.charAt(findChar) != ' ' && line.charAt(findChar) != '{' && line.charAt(findChar) != '|' && line.charAt(findChar) != '\'' && line.charAt(findChar) != '\"'){
                                    token += line.charAt(findChar);
                                }
                                else if(line.charAt(findChar) == '{'){
                                    break;
                                }
                            }
                            if(!token.contains("(*")){
                                bigExpression += "(" + "(null)" + token + ")|";
                                conjOperaciones.remove(token);
                                }
                            }
                        }
                    }
                }   
            }
            bigExpression = bigExpression.substring(0, bigExpression.length()-1);
            //System.out.println("BIG EXPRESSION: " + bigExpression);
            ArrayList<HashMap<String, String>> allMaps = new ArrayList<HashMap<String, String>>();
            allMaps.add(simpleIDs);
            allMaps.add(conjOperaciones);
            allMaps.add(simpleIDs);
            allMaps.add(conjOperaciones);
            List<Character> todo = Arrays.asList('*','|','^', '@','(',')','.');
            for(int mapIndex = 0; mapIndex < allMaps.size(); mapIndex++){
                for (int i = 0; i < bigExpression.length(); i++){
                    if(!todo.contains(bigExpression.charAt(i)) && bigExpression.charAt(i) != ' '){
                        String word = "";
                        for (int getWord = i; getWord < bigExpression.length(); getWord++){
                            if(!todo.contains(bigExpression.charAt(getWord)) && bigExpression.charAt(getWord) != ' '){
                                word += bigExpression.charAt(getWord);
                            }
                            else if((!word.equals("")) && (bigExpression.charAt(getWord) == ' ' || todo.contains(bigExpression.charAt(getWord)))){
                                String inicio = "";
                                if(i == 0){
                                    inicio = bigExpression.substring(0, 0);
                                }
                                else{
                                    inicio = bigExpression.substring(0, i);
                                }
                                String fin = bigExpression.substring(getWord, bigExpression.length());
                                if(allMaps.get(mapIndex).containsKey(word)){
                                    String replace = allMaps.get(mapIndex).get(word);
                                    bigExpression = inicio + replace + fin;
                                    i = inicio.length() + replace.length()-1;
                                    break;
                                }
                                else{
                                    bigExpression = inicio + word + fin;
                                    i = inicio.length() + word.length()-1;
                                    break;
                                }
                                
                            }
                        }
                    }
                    //postFix = postFix.replace(key, value);
                }
            }

            
            //ws b b d |c |*@ID @|e . e |ε |@f g - |ε |e @@ε |@NUMBER @|0 SEMICOLON @|1 ASSIGNOP @|2 LT @|3 EQ @|4 PLUS @|5 MINUS @|6 TIMES @|7 DIV @|8 LPAREN @|9 RPAREN @||
            System.out.println("NU:" + bigExpression);
            String newFormatBig = newFormatTree(bigExpression, '@');
            String postFix = newToPostFix(newFormatBig, '@');
            System.out.println("POSTFIX:" + postFix);
            newIDs.put("-1", postFix);
            reader.close();
            return newIDs;
            
        }catch (IOException e){
                System.err.println("Error reading file: " + e.getMessage());
                return null;
            }
    }

    public static ArrayList<String> processTokenAFN(ArrayList<HashMap<String,String>> results, String dir ){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dir));
            String line;
            HashMap<String, String> conjOperaciones = results.get(0);
            HashMap<String, String> simpleIDs = results.get(1);
            System.out.println(conjOperaciones);
            System.out.println(simpleIDs);
            String single = "";
            ArrayList<String> singleExpressions = new ArrayList<String>();
            HashMap<String, String> specialCase = new HashMap<String, String>();
            specialCase.put("+", "plusSymbol");
            specialCase.put("?", "questionSymbol");
            specialCase.put("(", "lparenSymbol");
            specialCase.put(")", "rparenSymbol");
            specialCase.put("|", "orSymbol");
            specialCase.put("*", "multiSymbol");
            specialCase.put("^", "elevadoSymbol");


            ArrayList<HashMap<String, String>> allMaps = new ArrayList<HashMap<String, String>>();
            allMaps.add(simpleIDs);
            allMaps.add(conjOperaciones);
            allMaps.add(simpleIDs);
            allMaps.add(conjOperaciones);
            List<Character> todo = Arrays.asList('*','|','^', '@','(',')','.');

            while ((line = reader.readLine()) != null) {
                if(line.contains("rule")){
                    while ((line = reader.readLine()) != null) {
                        //line = reader.readLine();
                        if(!line.isEmpty()){
                            if(line.contains("return")){
                                String token = "";
                                for (int findChar = 0; findChar < line.length(); findChar++){
                                    if(line.charAt(findChar) != ' ' && line.charAt(findChar) != '{' && line.charAt(findChar) != '|' && line.charAt(findChar) != '\'' && line.charAt(findChar) != '\"'){
                                        token += line.charAt(findChar);
                                    }
                                    else if(line.charAt(findChar) == '{'){
                                        break;
                                    }
                                }
                                if(conjOperaciones.containsKey(token)){
                                    single = "";
                                    int inicio = line.indexOf("{");
                                    int fin = line.indexOf("}");
                                    String dummy = line.substring(inicio+1, fin);
                                    dummy = dummy.replace(" ", "");
                                    dummy = dummy.replace("return", "");
                                    single = "((" + conjOperaciones.get(token) + ")" + dummy + ")";
                                    for(int mapIndex = 0; mapIndex < allMaps.size(); mapIndex++){
                                        for (int i = 0; i < single.length(); i++){
                                            if(!todo.contains(single.charAt(i)) && single.charAt(i) != ' '){
                                                String word = "";
                                                for (int getWord = i; getWord < single.length(); getWord++){
                                                    if(!todo.contains(single.charAt(getWord)) && single.charAt(getWord) != ' '){
                                                        word += single.charAt(getWord);
                                                    }
                                                    else if((!word.equals("")) && (single.charAt(getWord) == ' ' || todo.contains(single.charAt(getWord)))){
                                                        String inicioSingle = "";
                                                        if(i == 0){
                                                            inicioSingle = single.substring(0, 0);
                                                        }
                                                        else{
                                                            inicioSingle = single.substring(0, i);
                                                        }
                                                        String finSingle = single.substring(getWord, single.length());
                                                        if(allMaps.get(mapIndex).containsKey(word)){
                                                            if(specialCase.containsKey(allMaps.get(mapIndex).get(word))){
                                                                single = inicioSingle + specialCase.get(allMaps.get(mapIndex).get(word)) + "Special" + finSingle;
                                                                i = inicioSingle.length() + specialCase.get(allMaps.get(mapIndex).get(word)).length()-1;
                                                                break;
                                                            }
                                                            String replace = allMaps.get(mapIndex).get(word);
                                                            single = inicioSingle + replace + finSingle;
                                                            i = inicioSingle.length() + replace.length()-1;
                                                            break;
                                                        }
                                                        else{
                                                            single = inicioSingle + word + finSingle;
                                                            i = inicioSingle.length() + word.length()-1;
                                                            break;
                                                        }
                                                        
                                                    }
                                                }
                                            }
                                            //postFix = postFix.replace(key, value);
                                        }
                                    }
                                    singleExpressions.add(single);
                                }
                                else{
                                    single = "";
                                    int inicio = line.indexOf("{");
                                    int fin = line.indexOf("}");
                                    String dummy = line.substring(inicio+1, fin);
                                    dummy = dummy.replace(" ", "");
                                    dummy = dummy.replace("return", "");
                                    if(specialCase.containsKey(token)){
                                        single  = "((" + specialCase.get(token) + ")" + dummy + ")";
                                    }
                                    else{
                                        single  = "((" + token + ")" + dummy + ")";
                                    }
                                    singleExpressions.add(single);
                                }
                            }
                            else{
                                String token = "";
                            for (int findChar = 0; findChar < line.length(); findChar++){
                                if(line.charAt(findChar) != ' ' && line.charAt(findChar) != '{' && line.charAt(findChar) != '|' && line.charAt(findChar) != '\'' && line.charAt(findChar) != '\"'){
                                    token += line.charAt(findChar);
                                }
                                else if(line.charAt(findChar) == '{'){
                                    break;
                                }
                            }
                            if(!token.contains("(*")){
                                conjOperaciones.remove(token);
                                }
                            }
                        }
                    }
                }   
            }
            reader.close();
            System.out.println(singleExpressions);
            return singleExpressions;
            
        }catch (IOException e){
                System.err.println("Error reading file: " + e.getMessage());
                return null;
            }
    }

    public static String newFormatTree(String expresion, Character concat) {
        List<Character> todo = Arrays.asList('+','*','?','|','^');
        List<Character> notLetterOrNumber = Arrays.asList('+','*','?','|','^','(',')');
        String formateado = new String();
        

        for (int i = 0; i < expresion.length();i++) {
            Character char1 = expresion.charAt(i);
            String hashID = "";
            if(!notLetterOrNumber.contains(char1)){
                hashID += char1;
                for(int getID = i+1; getID < expresion.length(); getID++){
                    if(!notLetterOrNumber.contains(expresion.charAt(getID))){
                        hashID += expresion.charAt(getID);
                    }
                    else{
                        i += (getID - i - 1);
                        break;
                    }
                }
            }

            if (i + 1 < expresion.length()) {
                Character char2 = expresion.charAt(i+1);

                if(hashID.equals("")){
                    formateado += Character.toString(char1);
                }
                else{
                    formateado += hashID;
                }

                if ((!char1.equals('(') && !char2.equals(')') && !(char1 == '|' || char1 == '^') && !todo.contains(char2))) {
                    formateado += concat;
                }
            }
        }
        formateado += expresion.charAt(expresion.length() - 1);
        //System.out.println("Formateado:"+formateado);
        return formateado;
        //(a)|((b.(b|d|c)*).e)
        //((e@((.|e)|ε)@(f@(((g|-)|ε)@e)|ε))@x)
        //((a)@x)
        //xbbd|c|*%y%|
    }

    public static String newToPostFix(String expression, Character concat)
    {
        List<Character> operadores = Arrays.asList('|','(',')','*','^',concat);
        Stack<Character> stack = new Stack<>();
 
        String output = new String("");

        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
 

            if (!operadores.contains(c)){
                for(int getEverything = i; getEverything < expression.length(); getEverything++){
                    if(!operadores.contains(expression.charAt(getEverything))){
                        output += expression.charAt(getEverything);
                    }
                    else{
                        output += " ";
                        i += getEverything - i -1;
                        break;
                    }
                }
                
            }
                

            else if (c == '(')
                stack.push(c);
 
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    output += stack.pop();
 
                stack.pop();
            }
 
            else {
                while (!stack.isEmpty() && InFixPostFix.getPrecedence(c, concat) <= InFixPostFix.getPrecedence(stack.peek(), concat)) {
                    output += stack.pop();
                }
                stack.push(c);
            }
        }
 
        while (!stack.isEmpty()) {
            if (stack.peek() == '(')
                return "This expression is invalid";
            output += stack.pop();
        }
        return output;
    }

    public static String NewcambioMas(String expresion){
        List<Character> operadores = Arrays.asList('|','(',')','*','^');
        if(expresion.contains("+")){
            for(int i = 0; i < expresion.length(); i++){
                if(expresion.charAt(i) == '+' && expresion.charAt(i-1) != ')'){
                    for (int reverse = i; reverse >= 0; reverse--){
                        if(operadores.contains(expresion.charAt(reverse)) || reverse == 0){
                            expresion = expresion.substring(0, reverse) + "(" + expresion.substring(reverse, i) + ")" + expresion.substring(i, expresion.length());                     
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < expresion.length(); i++) {

            Character char1 = expresion.charAt(i);

            if (char1.equals('+')) {

                if (Character.toString(expresion.charAt(i-1)).equals(")")) {

                    int parentesis = 0;

                    for (int x = (i-1); x >= 0; x--) {
                        if ((x != (i-1)) && (Character.toString(expresion.charAt(x)).equals(")"))) {

                            parentesis++;

                        }

                        if ((Character.toString(expresion.charAt(x)).equals("("))) {
                            if (parentesis != 0) {
                                parentesis--;
                            } 
                            else {

                                String repetir = (String) expresion.subSequence(x, i);
                                expresion = expresion.substring(0, x) + repetir + repetir + "*" + expresion.substring(i+1);

                            }
                        }
                    }
                } 
                else {

                    String repetir = Character.toString(expresion.charAt(i-1));
                    expresion = expresion.substring(0, i-1) + repetir + repetir + "*" +expresion.substring(i+1);

                }
            }
        }
        return expresion;
    }


    public static String NewcambioInterrogacion(String expresion){
        List<Character> operadores = Arrays.asList('|','(',')','*','^');
        if(expresion.contains("?")){
            for(int i = 0; i < expresion.length(); i++){
                if(expresion.charAt(i) == '?' && expresion.charAt(i-1) != ')'){
                    for (int reverse = i; reverse >= 0; reverse--){
                        if(operadores.contains(expresion.charAt(reverse)) || reverse == 0){
                            expresion = expresion.substring(0, reverse) + "(" + expresion.substring(reverse, i) + ")" + expresion.substring(i, expresion.length());                     
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < expresion.length(); i++) {

            Character char1 = expresion.charAt(i);
            
            if (char1.equals('?')) {

                if (Character.toString(expresion.charAt(i-1)).equals(")")) {

                    for (int x = (i-1); x >= 0; x--) {

                        if ((Character.toString(expresion.charAt(x)).equals("("))) {
                            if (x != 0) {
                                
                                String cambioExpresion = (String) expresion.subSequence(x, i);
                                expresion = expresion.substring(0, x) + "(" + cambioExpresion + "|ε)" + expresion.substring(i+1);
                                break;

                            }
                        }
                    }
                    
                } 
                else {                    
                    expresion = expresion.substring(0, i-1) + "(" + Character.toString(expresion.charAt(i-1)) + "|ε)" + expresion.substring(i+1);
                }
            }
        }
        return expresion;
    }

    public static ArrayList<String> getLexExp(String filename){
        String dir = "C:\\Users\\Oscar\\Desktop\\Lab A\\" + filename;
        return processTokenAFN(processLet(dir), dir);
        

        
    }

    /*public static void main(String[] args)
    {
        //readYal();
        getLexExp();
    }*/

}

