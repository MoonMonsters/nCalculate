package com.chalmers.calculate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private int[] ids = new int[]{R.id.button0,R.id.button1,R.id.button2,R.id.button3,
            R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9,
            R.id.buttonClear,R.id.buttonDelete,R.id.buttonDiv,R.id.buttonMul,R.id.buttonSub,
            R.id.buttonAdd,R.id.buttonRes,R.id.buttonPointer};
    private Button[] buttons = new Button[ids.length];

    private TextView procedure = null;
    private TextView result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView(){
        ButtonListener listener = new ButtonListener();
        for(int i=0; i<buttons.length; i++){
            buttons[i] = (Button) findViewById(ids[i]);
            buttons[i].setOnClickListener(listener);
        }

        procedure = (TextView) findViewById(R.id.procedure);
        procedure.setText("0");
        result = (TextView) findViewById(R.id.result);
    }

    class ButtonListener implements View.OnClickListener{

        private StringBuilder builder = new StringBuilder("0");
        //存储后缀表达式及结果
        private Stack<String> resStack = new Stack<>();
        //存储运算符
        private Stack<String> opStack = new Stack<>();

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button0:
                    /*
                    限制0的个数，如果没有出现除了0意外的字符，那么则不添加0
                     */
                    Pattern pattern = Pattern.compile("[^0]+");
                    Matcher matcher = pattern.matcher(builder.toString());
                    if(matcher.find()) {
                        builder.append("0");
                    }
                    break;
                /**
                 * 直接追加数字字符
                 */
                case R.id.button1:
                    builder.append("1");
                    break;
                case R.id.button2:
                    builder.append("2");
                    break;
                case R.id.button3:
                    builder.append("3");
                    break;
                case R.id.button4:
                    builder.append("4");
                    break;
                case R.id.button5:
                    builder.append("5");
                    break;
                case R.id.button6:
                    builder.append("6");
                    break;
                case R.id.button7:
                    builder.append("7");
                    break;
                case R.id.button8:
                    builder.append("8");
                    break;
                case R.id.button9:
                    builder.append("9");
                    break;
                case R.id.buttonPointer:
                    //如果最后是两个字符，那么则替代
                    if(isOp(builder.charAt(builder.length()-1))){
                        builder.setCharAt(builder.length()-1,'.');
                    //否则追加
                    }else {
                        builder.append(".");
                    }
                    break;
                case R.id.buttonAdd:
                    //如果最后是两个字符，那么则替代
                    if(isOp(builder.charAt(builder.length()-1))){
                        builder.setCharAt(builder.length()-1,'+');
                        //否则追加
                    }else {
                        builder.append("+");
                    }
                    break;
                case R.id.buttonSub:
                    //如果最后是两个字符，那么则替代
                    if(isOp(builder.charAt(builder.length()-1))){
                        builder.setCharAt(builder.length()-1,'-');
                        //否则追加
                    }else {
                        builder.append("-");
                    }
                    break;
                case R.id.buttonMul:
                    //如果最后是两个字符，那么则替代
                    if(isOp(builder.charAt(builder.length()-1))){
                        builder.setCharAt(builder.length()-1,'*');
                        //否则追加
                    }else {
                        builder.append("*");
                    }
                    break;
                case R.id.buttonDiv:
                    //如果最后是两个字符，那么则替代
                    if(isOp(builder.charAt(builder.length()-1))){
                        builder.setCharAt(builder.length()-1,'/');
                        //否则追加
                    }else {
                        builder.append("/");
                    }
                    break;
                case R.id.buttonClear:
                    //删除所有的字符
                    builder.replace(0,builder.length(),"0");
                    //结果清空
                    result.setText("0.0");
                    //将栈清空
                    opStack.clear();
                    resStack.clear();
                    break;
                case R.id.buttonDelete:
                    /*
                    删除最后的字符
                    如果长度大于1，就删除，否则置为0
                     */
                    if(builder.length() > 1) {
                        builder.deleteCharAt(builder.length() - 1);
                    }else{
                        builder.replace(0,1,"0");
                    }
                    break;
                case R.id.buttonRes:
                    try {
                        calcutor();
                    }catch(ArithmeticException e){
                        result.setText("除数不能为0");
                    }catch(Exception e){
                        result.setText("出错");
                    }
//                    procedure.setText("0");
                    //将builder中存储的字符串清0
                    builder.replace(0,builder.length(),"0");
                    //将栈清空
                    opStack.clear();
                    resStack.clear();
                    break;
            }

            /**
             * 去掉首位的0
             * 如果字符串长度大于1，并且首位为0，第二位不是运算符
             */
            if(builder.length() > 1 && builder.charAt(0) == '0' && !isOp(builder.charAt(1))){
                builder.deleteCharAt(0);
            }
            procedure.setText(builder.toString());
        }

        /**
         * 判断字符是否为运算符
         * @param op
         * @return
         */
        public boolean isOp(char op){
            if(op == '+' || op=='-' || op=='*' || op=='/' || op == '.') {
                return true;
            }
            return false;
        }

        /**
         * 中缀表达式转换成后缀表达式，同时计算结果
         */
        public void calcutor(){
            String str = "";
            char ch;
            for(int i=0; i<builder.length(); i++){
                ch = builder.charAt(i);
                //如果是数字字符或者小数点，则直接添加到str后面
                if((ch>='0' && ch<='9') || ch=='.'){
                    str += ch;
                }else{
                    resStack.add(str);
                    Log.d("TAG",str);
                    str = "";
                    //如果栈空，则直接把运算符放进去
                    if(opStack.isEmpty()){
                        opStack.push(String.valueOf(ch));
                    //否则，比较两个运算符的优先级
                    }else{
                        char ch2 = opStack.pop().charAt(0);
                        //如果当前运算符的优先级小于或等于栈顶的运算符的优先级
                        //则ch2入resStack，ch入opStack
                        if(compareOp(ch) <= compareOp(ch2)) {
                            opStack.push(String.valueOf(ch));
                            //在入resStack时，结合队列中的数字，计算出结果
                            resStack.add(asmd(resStack.pop(),resStack.pop(),String.valueOf(ch2)));
                            //不发生改变，将ch入栈
                        }else{
                            opStack.push(String.valueOf(ch2));
                            opStack.push(String.valueOf(ch));
                        }
                    }
                }
            }

            resStack.add(str);
            //如果栈中还有运算符，则按顺序放到resStack中去，并同时计算
            while(!opStack.isEmpty()){
                String str2 = opStack.pop();
                resStack.add(asmd(resStack.pop(),resStack.pop(),str2));
            }

            result.setText(resStack.pop());
        }

        /**
         * 比较运算符的优先级
         * *与/比+和-的优先级高
         * @param op 运算符
         * @return
         */
        public int compareOp(char op){
            int result = 0;
            switch(op){
                case '+':
                case '-':
                    result = 0;
                    break;
                case '*':
                case '/':
                    result = 1;
                    break;
            }

            return result;
        }

        /**
         * 进行加减乘除运算
         * @return
         */
        public String asmd(String num1,String num2, String op) throws ArithmeticException{
            double res = 0;
            double n2 = Double.parseDouble(num1);
            double n1 = Double.parseDouble(num2);

            switch(op){
                case "+":
                    res = n1 + n2;
                    break;
                case "-":
                    res =  n1 - n2;
                    break;
                case "*":
                    res = n1 * n2;
                    break;
                case "/":
                    res = n1 / n2;
                    break;
            }

            return String.valueOf(res);
        }
    }
}