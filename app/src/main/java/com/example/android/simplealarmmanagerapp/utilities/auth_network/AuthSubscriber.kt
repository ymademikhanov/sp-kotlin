package com.example.android.simplealarmmanagerapp.utilities.auth_network

import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.utilities.network.Resource

interface AuthSubscriber {
    fun handleAuthUpdate(pair: Pair<Resource<Account>, String?>)
}