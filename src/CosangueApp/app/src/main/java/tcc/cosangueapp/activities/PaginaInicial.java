package tcc.cosangueapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tcc.cosangueapp.R;
import tcc.cosangueapp.adapters.RVAdapter;
import tcc.cosangueapp.daos.UsuarioDAO;
import tcc.cosangueapp.fragments.AcoesNaoEncontradasFragment;
import tcc.cosangueapp.fragments.PaginaInicialFragment;
import tcc.cosangueapp.gcm.GCloudMessaging;
import tcc.cosangueapp.json.Json;
import tcc.cosangueapp.pojos.Acao;
import tcc.cosangueapp.pojos.Usuario;
import tcc.cosangueapp.utils.Constantes;

public class PaginaInicial extends AppCompatActivity implements FolderChooserDialog.FolderCallback {

    UsuarioDAO usuarioDAO;
    FloatingActionButton fabInseriAcao;
    private Bundle bdUsuarioLogado;
    private Usuario usuarioLogado;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Toolbar mToolbar;
    private Drawer navegationDrawer;
    private AccountHeader headerNavegationDrawer;
    private SharedPreferences spPreferencias;
    private SharedPreferences.Editor editarPreferencias;
    private String genero;
    private String nome;
    private String login;
    private Long id;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private RVAdapter adapter;
    private List<Acao> listaAcoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_inicial);
        mToolbar = (Toolbar) findViewById(R.id.tb_pagina_inicial);
        mToolbar.setTitle("Feed de Eventos");
        setSupportActionBar(mToolbar);

        inicializaComponentes();

        replaceFragment(new PaginaInicialFragment());

        criaHeaderParaNavegationDrawer();
        criaNavegationDrawer(savedInstanceState);
        addItemsNoNavegationDrawer();

        String registrationId = spPreferencias.getString(Constantes.PROPERTY_REG_ID, null);
        if (registrationId == null) {
            new HttpRequestTaskVerificaRegistrationId().execute(getApplicationContext());
        }

        new HttpRequestTaskGetAllAcoes().execute();
    }

    public void inicializaComponentes() {

        //usuarioLogado = new Usuario();
        //bdUsuarioLogado = getIntent().getExtras();
        // verificaBundle();
        spPreferencias = getApplicationContext().getSharedPreferences(Constantes.NOME_SHARED_PREFERENCIES, MODE_APPEND);
        editarPreferencias = spPreferencias.edit();
        nome = spPreferencias.getString("nome", null);
        login = spPreferencias.getString("login", null);
        genero = spPreferencias.getString("genero", null);
        id = Long.parseLong(spPreferencias.getString("id", null));
        //  fabInseriAcao = (FloatingActionButton) findViewById(R.id.fab_inseri_acao);
    }

    private Context getContext() {
        return this;
    }

    private int imagemPerfil() {
        if (genero == "M") {
            return R.drawable.profile_masculino;
        } else {
            return R.drawable.profile_feminino;
        }
    }

    private void criaHeaderParaNavegationDrawer() {

        headerNavegationDrawer = new AccountHeaderBuilder()
                .withActivity(this)
                        //diminue o tamanho do header, mostra compactado - > .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(
                        new ProfileDrawerItem().withName(nome).withEmail(login).withIcon(imagemPerfil())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        //AQUI VAI A AÇÃO DE QUANDO ELE CLICA NO PROFILE QUE SERIA A IMAGEM DO LOGO, QUANDO ELE CLICAR LÁ
                        // ADICIONAMOS O EVENTO DO CLIQUE AQUI
                        // IR PARA CONFIGURAÇÕES DA CONTA OU PERFIL
                        // ABRIR ACTIVITY COM O NEGOCIO DE VOLTAR PARA TELA ANTERIOR , MAIS FACIL DE MEXER

                        return false;
                    }
                })
                .build();
    }

    private void criaNavegationDrawer(final Bundle savedInstanceState) {
        navegationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerNavegationDrawer)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(0)
                .withActionBarDrawerToggle(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = null;
                        if (drawerItem != null) {

                            if (position == 1) { // Perfil

                            }
                            if (position == 2) { // Hemocentro


                            }
                            if (position == 3) { // Eventos
                                replaceFragment(new PaginaInicialFragment());


                            }
                            if (position == 4) { // Doações
                                Toast.makeText(PaginaInicial.this, "Doações", Toast.LENGTH_LONG).show();
                            }
                            if (position == 6) { // Doação de Sangue

                                startActivity(new Intent(getApplicationContext(), Informacoes.class));
                            }
                            if (position == 8) { // Configuração
                                Toast.makeText(PaginaInicial.this, "Configurações", Toast.LENGTH_LONG).show();

                            }
                            if (position == 9) { // Logout
                                new HttpRequestTaskRemoveRegistrationId().execute(spPreferencias.getString("id", null));
                                limparSharedPreferences();
                                startActivity(new Intent(PaginaInicial.this, Inicio.class));

                            } else if (drawerItem instanceof Nameable) {
                                mToolbar.setTitle(((Nameable) drawerItem).getName().getText(PaginaInicial.this));
                            }
                        }
                        return false;
                    }
                })
                .build();
    }

    private void addItemsNoNavegationDrawer() {

        PrimaryDrawerItem iPerfil = new PrimaryDrawerItem().withName("Perfil").withIcon(R.drawable.perfil_icone);
        PrimaryDrawerItem iHemocentro = new PrimaryDrawerItem().withName("Hemocentro").withIcon(R.drawable.hemocentro_icone);
        PrimaryDrawerItem iEventos = new PrimaryDrawerItem().withName("Eventos").withIcon(R.drawable.evento_icone);
        PrimaryDrawerItem iDoacoes = new PrimaryDrawerItem().withName("Doações").withIcon(R.drawable.doacao_icone);
        PrimaryDrawerItem iConfiguracoes = new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.configuracao_icone);
        PrimaryDrawerItem iLogout = new PrimaryDrawerItem().withName("Logout").withIcon(R.drawable.logout_icone);
        PrimaryDrawerItem iDoacaoSangue = new PrimaryDrawerItem().withName("Doação de Sangue").withIcon(R.drawable.informacao_icone);
        navegationDrawer.addItem(iPerfil);
        navegationDrawer.addItem(iHemocentro);
        navegationDrawer.addItem(iEventos);
        navegationDrawer.addItem(iDoacoes);
        navegationDrawer.addItem(new SectionDrawerItem().withName("Saiba Mais"));
        navegationDrawer.addItem(iDoacaoSangue);
        navegationDrawer.addItem(new SectionDrawerItem().withName("Ajustes"));
        navegationDrawer.addItem(iConfiguracoes);
        navegationDrawer.addItem(iLogout);
    }

    private void replaceFragment(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_pagina_inicial, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

            }
            return false;
        }
        return true;
    }

    @Override
    public void onFolderSelection(File file) {
    }

    private void limparSharedPreferences() {
        editarPreferencias.clear();
        editarPreferencias.commit();
    }

    private class HttpRequestTaskInseriRegistrationId extends AsyncTask<String, String, Usuario> {

        @Override
        protected Usuario doInBackground(String... params) {
            try {
                usuarioDAO = new UsuarioDAO();
                usuarioDAO.updateRegistrationId(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class HttpRequestTaskRemoveRegistrationId extends AsyncTask<String, String, Usuario> {

        @Override
        protected Usuario doInBackground(String... params) {
            try {
                usuarioDAO = new UsuarioDAO();
                usuarioDAO.removeRegistrationId(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class HttpRequestTaskVerificaRegistrationId extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... params) {
            try {
                String registrationId = GCloudMessaging.register(params[0], Constantes.SENDER_ID);
                // adiciona o registration_id no shared preferences
                //editarPreferencias.putString(Constantes.PROPERTY_REG_ID, registrationId).commit();
                return registrationId;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String registrationId) {
            if (registrationId != null) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constantes.NOME_SHARED_PREFERENCIES, MODE_APPEND);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constantes.PROPERTY_REG_ID, registrationId).apply();

                boolean ok = checkPlayServices();
                if (ok) {
                    // Já está registrado
                    Usuario usuario = new Usuario();
                    usuario.setId(id);
                    usuario.setregistrationId(preferences.getString(Constantes.PROPERTY_REG_ID, null));
                    new HttpRequestTaskInseriRegistrationId().execute(usuario.getId().toString(), usuario.getregistrationId());
                }
            }
        }


    }

    private class HttpRequestTaskGetAllAcoes extends AsyncTask<Void, Void, List<Acao>> {

        @Override
        protected List<Acao> doInBackground(Void... params) {
            ArrayList<Acao> listaAcoes = new ArrayList<Acao>();
            Gson gson = new Gson();
            try {
                JSONObject resposta = Json.get(Constantes.URL_ACAO);
                if (resposta != null) {
                    Log.i("DEBUG", "JSON da lista não está vazio!");
                    JSONArray array = resposta.getJSONArray(Constantes.ROOT_ELEMENT_ACAO);
                    for (int i = 0; i < array.length(); i++) {
                        listaAcoes.add(gson.fromJson(array.get(i).toString(), Acao.class));
                    }
                    return listaAcoes;
                }

                return null;
            } catch (JSONException e) {
                try {
                    JSONObject resposta = Json.get(Constantes.URL_ACAO);
                    String unicaAcao = resposta.getJSONObject("acao").toString();
                    listaAcoes.add(gson.fromJson(unicaAcao, Acao.class));

                } catch (JSONException jE) {
                    jE.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Acao> retornoListaAcoes) {
            if (retornoListaAcoes != null) {
                Log.i("DEBUG", "Lista contém eventos!");
                listaAcoes = retornoListaAcoes;
                rv = (RecyclerView) findViewById(R.id.rv);
                llm = new LinearLayoutManager(getContext());
                rv.setLayoutManager(llm);
                adapter = new RVAdapter(listaAcoes, PaginaInicial.this);
                rv.setAdapter(adapter);
            } else {
                Log.i("DEBUG", "Lista de eventos vazia!");
                replaceFragment(new AcoesNaoEncontradasFragment());
            }
        }
    }
}


