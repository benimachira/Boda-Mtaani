/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.orion3shoppy.bodamtaani.controllers.mpesa;


/**
 * Created  on 5/28/2017.
 */

public class ApiUtils {
    //endpoint of my Api

//    public static final String BASE_URL = "https://api.safaricom.co.ke/";
//
    public static final String BASE_URL = "https://api.safaricom.co.ke/";


    public static STKPushService getTasksService(String token) {
        return RetrofitClient.getClient(BASE_URL, token).create(STKPushService.class);
    }
}
