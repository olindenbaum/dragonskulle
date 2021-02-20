package org.dragonskulle.network.components;

import com.google.flatbuffers.FlatBufferBuilder;
import org.dragonskulle.network.flatbuffers.FlatBufferHelpers;
import org.dragonskulle.network.proto.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class INetworkable {
    /**
     * Connects all sync vars in the Object to the network Object. This allows them to be updated.
     */

    final NetworkObject netObj;

    INetworkable(NetworkObject object) {
        this.netObj = object;
        System.out.println("netObj is assigned");
    }

    void dispose() {
        this.netObj.dispose();
    }

    void connectSyncVars() throws IllegalAccessException {
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> ISyncVar.class.isAssignableFrom(field.getType())).collect(Collectors.toList());
        int[] sync_vars = new int[fields.size()];
        int i = 0;
        System.out.println("No. Fields: " + fields.size());
        FlatBufferBuilder builder = new FlatBufferBuilder(512);
        int built_offset;


        for (Field f : fields) {
            ISyncVar sv = (ISyncVar) f.get(this); //retrieves syncvar from child and adds it to the connection array
            //create single sync var to add to array
            int sync_var_id = FlatBufferHelpers.ISyncVar2flatb(builder, sv);
            sync_vars[i++] = sync_var_id;
            System.out.println("adding " + f.getName());
        }

        int net_id_offset = builder.createString(this.netObj.netID);
        int sync_vars_vector_offset = RegisterSyncVarsRequest.createSyncVarsVector(builder, sync_vars);
        RegisterSyncVarsRequest.startRegisterSyncVarsRequest(builder);
        RegisterSyncVarsRequest.addNetId(builder, net_id_offset);
        RegisterSyncVarsRequest.addSyncVars(builder, sync_vars_vector_offset);
        RegisterSyncVarsRequest.addIsDormant(builder, this.netObj.isDormant);
        int request_offset = RegisterSyncVarsRequest.endRegisterSyncVarsRequest(builder);
        builder.finish(request_offset);
        byte[] sync_vars_buf = builder.sizedByteArray();
        System.out.println("syncvars added to payload");
        System.out.println("FlatBuffer :: " + Arrays.toString(sync_vars_buf));
        netObj.registerSyncVars(sync_vars_buf);
    }
}


