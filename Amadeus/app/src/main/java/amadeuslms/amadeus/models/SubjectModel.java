/*
Copyright 2016, 2017 UFPE - Universidade Federal de Pernambuco
 
Este arquivo é parte do programa Amadeus Sistema de Gestão de Aprendizagem, ou simplesmente Amadeus LMS
 
O Amadeus LMS é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como publicada pela Fundação do Software Livre (FSF); na versão 2 da Licença.
 
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 
Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o título "LICENSE", junto com este programa, se não, escreva para a Fundação do Software Livre (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.
*/
package amadeuslms.amadeus.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by zambom on 30/06/17.
 */

public class SubjectModel implements Parcelable {

    private String name, slug;
    private boolean visible;
    private int notifications;

    public SubjectModel(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);

        this.setName(data[0]);
        this.setSlug(data[1]);
        this.setVisible(Boolean.parseBoolean(data[2]));
        this.setNotifications(Integer.parseInt(data[3]));
    }

    public SubjectModel(String name, String slug, boolean visible, int notifications) {
        this.name = name;
        this.slug = slug;
        this.visible = visible;
        this.notifications = notifications;
    }

    public static final Parcelable.Creator<SubjectModel> CREATOR = new Parcelable.Creator<SubjectModel>() {
        @Override
        public SubjectModel createFromParcel(Parcel source) {
            return new SubjectModel(source);
        }

        @Override
        public SubjectModel[] newArray(int size) {
            return new SubjectModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.getName(),
                this.getSlug(),
                String.valueOf(this.isVisible()),
                String.valueOf(this.getNotifications())
        });
    }
}
