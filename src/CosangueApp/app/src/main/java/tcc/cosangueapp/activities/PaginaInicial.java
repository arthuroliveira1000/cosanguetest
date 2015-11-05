package tcc.cosangueapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

import java.io.File;

import tcc.cosangueapp.R;
import tcc.cosangueapp.daos.UsuarioDAO;
import tcc.cosangueapp.fragments.EventosDetalhesFragment;
import tcc.cosangueapp.fragments.HemocentroFragment;
import tcc.cosangueapp.fragments.PaginaInicialFragment;
import tcc.cosangueapp.gcm.GCloudMessaging;
import tcc.cosangueapp.pojos.Usuario;
import tcc.cosangueapp.utils.Constantes;

public class PaginaInicial extends AppCompatActivity implements PaginaInicialFragment.OnFragmentInteractionListener,
        FolderChooserDialog.FolderCallback {

    UsuarioDAO usuarioDAO;
    private Bundle bdUsuarioLogado;
    private Usuario usuarioLogado;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Toolbar mToolbar;
    private Drawer navegationDrawer;
    private AccountHeader headerNavegationDrawer;
    private FrameLayout flContent;
    private SharedPreferences spPreferencias;
    private SharedPreferences.Editor editarPreferencias;
    private String genero;
    private String nome;
    private String login;
    private Long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_inicial);
        mToolbar = (Toolbar) findViewById(R.id.tb_pagina_inicial);
        mToolbar.setTitle("Feed de Eventos");
        setSupportActionBar(mToolbar);
        inicializaComponentes();


        criaHeaderParaNavegationDrawer();
        criaNavegationDrawer(savedInstanceState);
        addItemsNoNavegationDrawer();

        //verificaRegistrationId();

        String registrationId = spPreferencias.getString(Constantes.PROPERTY_REG_ID, null);

        // caso o reg_id ainda não esteja salvo no shared preferences, chamar a async task pra coloca-lo dentro
        if (registrationId == null) {
            new HttpRequestTaskVerificaRegistrationId().execute(getApplicationContext());
            // verificaRegistrationId();
          /*  boolean ok = checkPlayServices();
            if (ok) {
                // Já está registrado
                Usuario usuario = new Usuario();
                usuario.setId(id);
                usuario.setregistrationId(spPreferencias.getString(Constantes.PROPERTY_REG_ID, null));
                new HttpRequestTask().execute(usuario);
            }*/

        }


       /* if (registrationId != null) {
            usuarioLogado.setId(id);
            new HttpRequestTask().execute(usuarioLogado);
        }
*/
        // Quando iniciar, lê a msg da notification
        //String msg = getIntent().getStringExtra("msg");
        //Log.d("Mensagem recebida", msg);

    }

    public void inicializaComponentes() {

        usuarioLogado = new Usuario();
        bdUsuarioLogado = getIntent().getExtras();
        verificaBundle();
        flContent = (FrameLayout) findViewById(R.id.fl_pagina_inicial);
        spPreferencias = getApplicationContext().getSharedPreferences(Constantes.NOME_SHARED_PREFERENCIES, MODE_APPEND);
        editarPreferencias = spPreferencias.edit();
        nome = spPreferencias.getString("nome", null);
        login = spPreferencias.getString("login", null);
        genero = spPreferencias.getString("genero", null);
        id = Long.parseLong(spPreferencias.getString("id", null));

    }

    private void verificaBundle() {

        if (bdUsuarioLogado != null) {
            if (bdUsuarioLogado.containsKey("usuario")) {
                usuarioLogado = (Usuario) bdUsuarioLogado.getSerializable("usuario");
            }
        }
    }

    private void verificaRegistrationId() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String registrationId = GCloudMessaging.register(getApplicationContext(), Constantes.SENDER_ID);

                SharedPreferences spPreferencias = getApplicationContext().getSharedPreferences(Constantes.NOME_SHARED_PREFERENCIES, MODE_APPEND);
                SharedPreferences.Editor editarPreferencias = spPreferencias.edit();
                // adiciona o registration_id no shared preferences
                editarPreferencias.putString(Constantes.PROPERTY_REG_ID, registrationId).apply();

            }
        }.start();
    }

    private Context getContext() {
        return this;
    }

    /*private String retornaRegistrationId() {

        boolean ok = checkPlayServices();
        if (ok) {
            // Já está registrado
            return GCloudMessaging.getRegistrationId(getContext());

        } else {
            return null;
        }
        //fazer put no usuário  para adicionar seu novo registration_id
        //criar método para receber o json recebido na mensagem
    }*/

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
                                Toast.makeText(PaginaInicial.this, "Testando Perfil", Toast.LENGTH_LONG).show();
                            }
                            if (position == 2) { // Hemocentro
                                replaceFragment(new HemocentroFragment());
                            }
                            if (position == 3) { // Eventos
                                replaceFragment(new EventosDetalhesFragment());

                            }
                            if (position == 4) { // Doações
                                Toast.makeText(PaginaInicial.this, "Doações", Toast.LENGTH_LONG).show();
                            }
                            if (position == 6) { // Doação de Sangue

                                startActivity(new Intent(getApplicationContext(), Informacoes.class));
                                // start activity(informacoes)


                                // /replaceFragment(new InformacoesFragment());
                                //Toast.makeText(PaginaInicial.this, "Informações", Toast.LENGTH_LONG).show();
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
        // Exemplo switchDrawerItem  -> navegationDrawerLeft.addItem(new SwitchDrawerItem().withName("Notificação").withChecked(true).withOnCheckedChangeListener(mOnOnCheckedChangeListener));
        //Exemplo ToggleDrawerItem - > navegationDrawerLeft.addItem(new ToggleDrawerItem().withName("News").withChecked(true).withOnCheckedChangeListener(mOnOnCheckedChangeListener));

    }

    private void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_pagina_inicial, frag, "TAG").commit();
        getSupportFragmentManager().beginTransaction().addToBackStack(null);
    }

    public void onFragmentInteractionListener() {
        //you can leave it empty
    }

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        // Quando receber uma nova notificação, vai substituir a fragment da pagina inicial pela dos eventos
        // depois vai pega e evento da bundle e passar junto pra fragment
        // daí lá na fragment vai criar um card view com o evento e seus dados


        // Quando iniciar, lê a msg da notification
        // pegar o json e mostrar na tela
        String msg = intent.getStringExtra("msg");
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //toast("Este aparelho não suporta o Google Play Services.");
                // resolver o que fazer quando o celular não tem suporte pro google play services, ele não receberá as notificações
                // dos eventos, terá que entrar no aplicativo para ver se tem novos eventos ou não
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

}

