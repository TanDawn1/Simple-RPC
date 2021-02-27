package serializerType;

public enum SerializerCode {

    JSON(1);

    public final int code;

    SerializerCode(int code){
        this.code = code;
    }
}
