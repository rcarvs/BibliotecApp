package br.com.rcarvs.bibliotecappufsj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LivrosActivity extends Activity{

    private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livros);
        Intent intent = getIntent();
        findViewById(R.id.semResultadosPanel).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        final UsuarioManager user = new UsuarioManager();
        user.setTokenAfter(intent.getStringExtra("TokenAfter"));
        user.setTokenBefore(intent.getStringExtra("TokenBefore"));
        user.setLivros(intent.getStringExtra("livros"));
        user.setNome(intent.getStringExtra("nome"));
        user.setLogin(intent.getStringExtra("login"));
        user.setSenha(intent.getStringExtra("senha"));

        String[] splitNome = user.getNome().split(" ");
        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("OLÁ "+splitNome[0]);
        TextView welcomeNoResult = (TextView) findViewById(R.id.txtNomeUsuarioNoLivros);
        welcomeNoResult.setText("OLÁ "+splitNome[0]);

        final ListView listView = (ListView) findViewById(R.id.listaLivros);
        if(user.getLivros() != null && user.getLivros().length() > 0) {
            String[] livros = user.getLivros().split("-%¬@-");
            final List<ItemListView> listaLivros = new ArrayList<ItemListView>();
            for(String livro : livros){

                String[] livroSerializado = livro.split("-%¬¹-");
                String[] dataVencimentoSerializada = livroSerializado[2].split("-");
                String dia = dataVencimentoSerializada[0];
                String mes = dataVencimentoSerializada[1];
                String ano = dataVencimentoSerializada[2];
                SimpleDateFormat anoFormat = new SimpleDateFormat("yyyy");
                if(ano.length() == 2){
                    ano = "20"+ano;
                }else{
                    Date data = new Date();
                    ano = anoFormat.format(data);
                }
                String dataVencimentoFormatada=dia+"/"+mes+"/"+ano;

                ItemListView item = new ItemListView(livroSerializado[0],livroSerializado[1],dataVencimentoFormatada);
                listaLivros.add(item);
            }

            listView.setVerticalScrollBarEnabled(true);


            final AdapterListView adapter = new AdapterListView(listaLivros);


            listView.setAdapter(adapter);
            //ArrayAdapter<String> livrosAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,livros);
            //listView.setAdapter(livrosAdapter);

            listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    ItemListView itemClicado = listaLivros.get(position);
                    String urlRenovacao = "http://www.dibib.ufsj.edu.br/cgi-bin/wxis.exe?IsisScript=phl82/037.xis&mfn="+itemClicado.getCodigo()+"&acv=001&tmp="+user.getTokenAfter();
                    RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,urlRenovacao,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    int duracao = Toast.LENGTH_SHORT;
                                    if (response.length() > 0) {
                                        Log.d("RevacaoHTML",response);
                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                        if(response.contains("COMPROVANTE DE RENOVAÇÃO")){
                                            Toast toast = Toast.makeText(getApplicationContext(), "Renovado com sucesso!",duracao);
                                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.show();

                                            TextView dataVencimento = (TextView) view.findViewById(R.id.dataVencimento);
                                            String id_devolver_em = "Devolver em: ";
                                            Integer posicao_devolver_inicio = response.indexOf(id_devolver_em);
                                            Integer posicao_devolver_fim = response.indexOf("<br>",posicao_devolver_inicio);
                                            String devolver_rodada = response.substring(posicao_devolver_inicio+id_devolver_em.length(),posicao_devolver_fim);
                                            dataVencimento.setText("Vence em: "+devolver_rodada);

                                            SharedPreferences settings = getSharedPreferences("RateInfo", 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            int qtdeRenovada = (Integer.parseInt(settings.getString("qtdeRenovada","0")));
                                            String qtdeRenovadaStr = Integer.toString(qtdeRenovada);
                                            editor.putString("qtdeRenovada",settings.getString("qtdeRenovada",qtdeRenovadaStr));
                                            editor.putString("avaliou",settings.getString("avaliou","N"));
                                            editor.commit();

                                            if(settings.getString("avaliou","N") == "N"){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                                builder.setMessage("Olá! Este é o 10º livro que você renova por aqui!/n Está gostando? Que tal deixar seu comentário no Google Play? /n Desenvolvedores independentes adoram receber feedbacks!")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Desejo Avaliar!", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                                try {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("Agora não :(", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            }
                                        }else{
                                            //tenta pegar a mensagem que o sistema trouxe
                                            String id_mensagem = "<body><h2>";
                                            Integer posicao_msg_inicio = response.indexOf(id_mensagem);
                                            Integer posicao_msg_fim = response.indexOf("</h2>",posicao_msg_inicio);
                                            String msg_rodada = response.substring(posicao_msg_inicio+id_mensagem.length(),posicao_msg_fim);
                                            if(msg_rodada.length()> 0 && msg_rodada.length() < 10000){
                                                Toast toast = Toast.makeText(getApplicationContext(), msg_rodada,Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                                toast.show();
                                            }else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Infelizmente não foi possível renovar este livro :( Tente novamente...", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                                toast.show();
                                            }
                                        }



                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Houve um erro ao tentar renovar pois não houve conexão disponível :( Tente novamente mais tarde!",Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.show();
                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Houve um erro ao tentar renovar pois não houve conexão disponível :( Tente novamente mais tarde!",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            60000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);

                }
            });

        }else{

            findViewById(R.id.semResultadosPanel).setVisibility(View.VISIBLE);
        }




    }




}
