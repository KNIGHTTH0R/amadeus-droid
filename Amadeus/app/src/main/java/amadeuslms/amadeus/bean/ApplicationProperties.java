/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.bean;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 16/06/17.
 */

public class ApplicationProperties {

    private static Properties applicationProperties;

    public static void getApplicationProperties(Context context) {
        if (applicationProperties == null) {
            try {
                applicationProperties = new Properties();

                InputStream rawSource = context.getResources().openRawResource(R.raw.application);
                applicationProperties.load(rawSource);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String getWebServiceURL(Context context){
        getApplicationProperties(context);

        return applicationProperties.getProperty("webservice.url");
    }

    public static void setWebServiceURL(Context context, String url) {
        getApplicationProperties(context);

        applicationProperties.setProperty("webservice.url", url);
    }
}
