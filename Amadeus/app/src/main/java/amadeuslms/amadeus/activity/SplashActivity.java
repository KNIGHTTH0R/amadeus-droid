/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import amadeuslms.amadeus.cache.TokenCacheController;
import amadeuslms.amadeus.cache.UserCacheController;

/**
 * Created by zambom on 09/06/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMEOUT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;

                if (!UserCacheController.hasUserCache(context)) {
                    intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(context, HomeActivity.class);
                    if(TokenCacheController.hasTokenCache(context) && TokenCacheController.getTokenCache(context).isToken_expired()) {
                        TokenCacheController.getTokenCache(context).startRenewToken(intent, context);
                    } else {
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, SPLASH_TIMEOUT);

    }
}
