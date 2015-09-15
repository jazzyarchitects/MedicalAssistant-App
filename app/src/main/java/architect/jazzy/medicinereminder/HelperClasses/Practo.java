package architect.jazzy.medicinereminder.HelperClasses;

/**
 * Created by Jibin_ism on 15-Sep-15.
 */
public class Practo{
    private static final String apiHost="https://api.practo.com";

    private static final String DOCTOR_LIST=apiHost+"/doctors/?page=";

    //https://api.practo.com/doctors/<id>?with_relations=<true/false>
    private static final String DOCTOR_DETAIL=apiHost+"/doctors/";

    //https://api.practo.com/practices/?pege=<page number>

    /**
     * https://api.practo.com/search/?city=<city name>&locality=<locality>
     *     &speciality=<speciality>&searchfor=<criteria>&q=<name>&offset=<offset>
     *         &near=<latitude>,<longitude>&sort_by=<criteria>
     *
     *             use either near or locallity
     *             search for criteria: "specialization","doctor","practice"
     *             speciality = some speciality
     *             q=query. depends on searchfor
     *             near= lat, long in decimal degrees
     *             sort_by="consultation_fees","practo_ranking", "experience_years","recommendation"
     *
     *
     * **/
    public static String getCustomSearchUrl(String city,String locality,String speciality, String searchFor, String query, int offset, String sortBy){
        return apiHost+"/search/?city="+city+"&locality="+locality+"&speciality="+speciality+
                "&searchfor="+searchFor+"&q="+query+"&offset="+offset+"&sort_by="+sortBy;
    }

    public static String getDoctorListUrl(){
        return getDoctorListUrl(1);
    }
    public static String getDoctorListUrl(int pageNumber){
        return DOCTOR_LIST+pageNumber;
    }

    public static String getDoctorDetailUrl(String doctorId){
        return getDoctorDetailUrl(doctorId,false);
    }
    public static String getDoctorDetailUrl(String doctorId, boolean with_relations){
        return DOCTOR_DETAIL+doctorId+"?with_relations="+String.valueOf(with_relations);
    }


    public static final String SEARCH_FOR_SPECIALIZATION="specialization";
    public static final String SEARCH_FOR_DOCTOR="doctor";
    public static final String SEARCH_FOR_PRACTICE="practice";

    public static final String SORT_BY_FEES="consultation_fees";
    public static final String SORT_BY_PRACTO_RANKING="practo_ranking";
    public static final String SORT_BY_EXPERIENCE="experience_years";
    public static final String SORT_BY_RECOMMENDATION="recommendation";


}
