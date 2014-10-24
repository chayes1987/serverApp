
public enum ERROR_CODE {
    OK(200), INTERNAL_SERVER_ERROR(500), FILE_NOT_FOUND(404);
    private int code;
    ERROR_CODE(int code){
        this.code = code;
    }
}
