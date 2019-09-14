package br.com.rcarvs.bibliotecappufsj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    EditText editTextLogin, editTextSenha;
    ImageView botaoEntrar;
    TextView textoErroLogin;
    CheckBox manterLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }

        setContentView(R.layout.activity_main);


        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        textoErroLogin = (TextView) findViewById(R.id.textErro);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextSenha = (EditText) findViewById(R.id.editTextSenha);

        textoErroLogin.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("UsuarioInfo", 0);
        editTextLogin.setText(settings.getString("Login", "").toString());
        editTextSenha.setText(settings.getString("Senha", "").toString());

        //prossegue somente se possuir conexao com a internet
        if(this.isOnline(this)){

        /*
        editTextLogin.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editTextLogin.setTextColor(Color.parseColor("#FFFFFF"));
                editTextLogin.setHintTextColor(Color.parseColor("#FFFFFF"));
                return false;
            }
        });

        editTextSenha.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editTextSenha.setTextColor(Color.parseColor("#FFFFFF"));
                editTextSenha.setHintTextColor(Color.parseColor("#FFFFFF"));
                return false;
            }
        });
        */

            botaoEntrar = (ImageView) findViewById(R.id.botaoEntrar);
            botaoEntrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    editTextLogin = (EditText) findViewById(R.id.editTextLogin);
                    editTextSenha = (EditText) findViewById(R.id.editTextSenha);
                    manterLogado = (CheckBox) findViewById(R.id.checkBoxSalvar);
                    if(editTextLogin.length() == 0){
                        textoErroLogin.setText("• Digite o número da matrícula.");
                        textoErroLogin.setVisibility(View.VISIBLE);

                    }else if(editTextSenha.length() == 0){
                        textoErroLogin.setText("• Digite a senha de acesso.");
                        textoErroLogin.setVisibility(View.VISIBLE);

                    }else{
                        if(manterLogado.isChecked()) {
                            SharedPreferences settings = getSharedPreferences("UsuarioInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Login",editTextLogin.getText().toString());
                            editor.putString("Senha",editTextSenha.getText().toString());
                            editor.commit();
                        }else{
                            SharedPreferences settings = getSharedPreferences("UsuarioInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Login","");
                            editor.putString("Senha","");
                            editor.commit();
                        }

                        String urlForTmpBefore = "http://www.dibib.ufsj.edu.br/cgi-bin/wxis.exe?IsisScript=phl82.xis&cipar=phl82.cip&lang=por";

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET,urlForTmpBefore,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.length() > 0) {
                                            Integer posicao_inicio_tmp = response.indexOf("tmp=");
                                            if (posicao_inicio_tmp != 0) {
                                                Integer posicao_fim_tmp = response.indexOf("\"", posicao_inicio_tmp);
                                                if (posicao_fim_tmp != 0) {
                                                    final String tmp_value = (String) response.subSequence(posicao_inicio_tmp + 4, posicao_fim_tmp);

                                                    String urlForPostLogin = "http://www.dibib.ufsj.edu.br/cgi-bin/wxis.exe";

                                                    HttpParams parametroConexao = new BasicHttpParams();
                                                    HttpConnectionParams.setConnectionTimeout(parametroConexao,25000);
                                                    HttpConnectionParams.setSoTimeout(parametroConexao,60000);

                                                    HttpClient httpClient = new DefaultHttpClient(parametroConexao);
                                                    HttpPost httpPost = new HttpPost(urlForPostLogin);

                                                    List<NameValuePair> params = new ArrayList<NameValuePair>(5);
                                                    params.add(new BasicNameValuePair("IsisScript", "phl82/026.xis"));
                                                    params.add(new BasicNameValuePair("login", editTextLogin.getText().toString()));
                                                    params.add(new BasicNameValuePair("pwd", editTextSenha.getText().toString()));
                                                    params.add(new BasicNameValuePair("submitter", "Processando..."));
                                                    params.add(new BasicNameValuePair("tmp", tmp_value));
                                                    try {
                                                        httpPost.setEntity(new UrlEncodedFormEntity(params));
                                                    }catch (UnsupportedEncodingException ex){
                                                        ex.printStackTrace();
                                                    }

                                                    try {
                                                        HttpResponse respostaPost = httpClient.execute(httpPost);
                                                        String htmlResponse = EntityUtils.toString(respostaPost.getEntity());
                                                        if(htmlResponse.length() > 0){
                                                            Integer posicao_inicio_tmp_pos = htmlResponse.indexOf("tmp=");
                                                            if (posicao_inicio_tmp_pos != 0) {
                                                                Integer posicao_fim_tmp_pos = htmlResponse.indexOf("\"", posicao_inicio_tmp_pos);
                                                                if (posicao_fim_tmp_pos != 0) {
                                                                    final String tmp_value_pos = (String) htmlResponse.subSequence(posicao_inicio_tmp_pos + 4, posicao_fim_tmp_pos);
                                                                    if(tmp_value_pos.length() > 0){

                                                                        Boolean tem_senha_incorreta = htmlResponse.contains("Senha Inválida");

                                                                        if(tem_senha_incorreta == true){
                                                                            senhaIncorreta();
                                                                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                        }else{
                                                                            //to logadão fera

                                                                            String urlExratoRenovacao = "http://www.dibib.ufsj.edu.br/cgi-bin/wxis.exe?IsisScript=phl82/017.xis&tmp="+tmp_value_pos;


                                                                            RequestQueue queueExtrato = Volley.newRequestQueue(getApplicationContext());

                                                                            StringRequest stringRequest = new StringRequest(Request.Method.GET,urlExratoRenovacao,
                                                                                    new Response.Listener<String>() {
                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            if(response.length() > 0) {

                                                                                                //Primeira coisa: Pegar o nome do usuário

                                                                                                Boolean tem_nome_usuario = response.contains("Nome:");
                                                                                                if(tem_nome_usuario == true){
                                                                                                    Integer posicao_inicio_nome_usuario = response.indexOf("Nome:");
                                                                                                    Integer posicao_fim_nome_usuario = response.indexOf("<br>",posicao_inicio_nome_usuario);
                                                                                                    String nome_usuario = (String) response.subSequence(posicao_inicio_nome_usuario + 6, posicao_fim_nome_usuario);
                                                                                                    String expressao_unica_livros = "wxis.exe?IsisScript=phl82/037.xis&mfn=";
                                                                                                    String livros = "";

                                                                                                    Integer index = 0;
                                                                                                    for (index = response.indexOf(expressao_unica_livros,index);index>=0;index=response.indexOf(expressao_unica_livros,index+1)){

                                                                                                        //Primeira coisa é achar o identificador
                                                                                                        Integer posicao_identificador_inicio = response.indexOf("mfn=",index);

                                                                                                        Integer posicao_identificador_fim = response.indexOf("&",posicao_identificador_inicio);

                                                                                                        String identificador_rodada = response.substring(posicao_identificador_inicio+4,posicao_identificador_fim);


                                                                                                        String id_devolver_em = "Devolver em: ";

                                                                                                        Integer posicao_devolver_inicio = response.indexOf(id_devolver_em,index);

                                                                                                        Integer posicao_devolver_fim = response.indexOf("</td>",posicao_devolver_inicio);

                                                                                                        String devolver_rodada = response.substring(posicao_devolver_inicio+id_devolver_em.length(),posicao_devolver_fim);


                                                                                                        String id_nome = "<tr><td wdith=\"5%\"></td><td width=\"95%\" align=\"left\">";

                                                                                                        Integer posicao_nome_inicio = response.indexOf(id_nome,posicao_devolver_fim);

                                                                                                        Integer posicao_nome_fim = response.indexOf("</td>",posicao_nome_inicio+id_nome.length());

                                                                                                        String nome_rodada = response.substring(posicao_nome_inicio+id_nome.length(),posicao_nome_fim).replace(System.getProperty("line.separator").toString(),"");
                                                                                                        //Separador de parametros internos = -%¬¹-
                                                                                                        //Separador de parametros externos = -%¬@-
                                                                                                        livros = livros+identificador_rodada+"-%¬¹-"+nome_rodada+"-%¬¹-"+devolver_rodada+"-%¬@-";
                                                                                                    }


                                                                                                    if(livros.length() > 0){
                                                                                                        livros = livros.substring(0,livros.length()-6);
                                                                                                    }
                                                                                                    UsuarioManager user = new UsuarioManager();
                                                                                                    user.setTokenAfter(tmp_value_pos);
                                                                                                    user.setTokenBefore(tmp_value);
                                                                                                    user.setSenha(editTextSenha.getText().toString());
                                                                                                    user.setLogin(editTextLogin.getText().toString());
                                                                                                    user.setNome(nome_usuario);
                                                                                                    user.setLivros(livros);
                                                                                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                                                                                                    Intent intent = new Intent(MainActivity.this,LivrosActivity.class);
                                                                                                    intent.putExtra("TokenAfter",tmp_value_pos);
                                                                                                    intent.putExtra("TokenBefore",tmp_value);
                                                                                                    intent.putExtra("login",editTextLogin.getText().toString());
                                                                                                    intent.putExtra("senha",editTextSenha.getText().toString());
                                                                                                    intent.putExtra("nome",nome_usuario);
                                                                                                    intent.putExtra("livros",livros);
                                                                                                    MainActivity.this.startActivity(intent);

                                                                                                }else{
                                                                                                    acessoIncorreto();
                                                                                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                                                }
                                                                                            }else{
                                                                                                acessoIncorreto();
                                                                                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                                            }
                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    Log.d("Erro de conexão:",error.toString());
                                                                                    acessoIncorreto();
                                                                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                                                    60000,
                                                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                                            queueExtrato.add(stringRequest);




                                                                        }
                                                                    }else{
                                                                        acessoIncorreto();
                                                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                    }

                                                                }else{
                                                                    acessoIncorreto();
                                                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                                }
                                                            }else{
                                                                acessoIncorreto();
                                                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                            }
                                                        }else{
                                                            acessoIncorreto();
                                                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                        }

                                                    } catch (ClientProtocolException e) {
                                                        acessoIncorreto();
                                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                    } catch (IOException e) {
                                                        acessoIncorreto();
                                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                    }

                                                }else{
                                                    acessoIncorreto();
                                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                                }
                                            }else{
                                                acessoIncorreto();
                                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                            }
                                        }else{
                                            acessoIncorreto();
                                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Erro de conexão:",error.toString());
                                acessoIncorreto();
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                60000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(stringRequest);


                    }




                }
            });

        }else{
            this.semInternetAction(this);

        }




    }

    public void semInternetAction(Context context){
        alertDialogWithButton(context, "Erro", "O aplicativo precisa de conexão com a internet para funcionar! Que tal se conectar?", "Conectar", android.R.drawable.ic_dialog_alert, new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    public void senhaIncorreta(){
        textoErroLogin.setText("Login e/ou senha informados estão incorretos.");
        textoErroLogin.setVisibility(View.VISIBLE);

    }

    public void acessoIncorreto(){
        textoErroLogin.setText("Houve um erro ao buscar as informações... Tente novamente mais tarde!");
        textoErroLogin.setVisibility(View.VISIBLE);
    }
    public void alertDialogWithButton(Context context,String Title,String Message,String BotaoMsg,int Icon,final Intent acao){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setIcon(Icon);
        alertDialog.setButton(BotaoMsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(acao, 0);
            }
        });


        alertDialog.show();
    }




    public static boolean isOnline(MainActivity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }else {
            return false;
        }
    }


}