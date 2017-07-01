package amadeuslms.amadeus.response;

import amadeuslms.amadeus.lists.SubjectList;

/**
 * Created by zambom on 30/06/17.
 */

public class SubjectResponse extends GenericResponse {

    private SubjectList data;

    public SubjectList getData() {
        return data;
    }

    public void setData(SubjectList data) {
        this.data = data;
    }
}
