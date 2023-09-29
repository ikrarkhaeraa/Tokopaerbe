// package com.example.tokopaerbe
//
// @Module
// @InstallIn(SingletonComponent::class)
// object AppModule {
//    @Provides
//    @Singleton
//    fun provideApiService(): ApiService {
//        val loggingInterceptor =
//            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://172.17.20.235:5000/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//
//        return retrofit.create(ApiService::class.java)
//    }
// }
//
// @Module
// @InstallIn(SingletonComponent::class)
// object PreferencesModule {
//    @Provides
//    @Singleton
//    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
//        val database: DataStore<Preferences> by context.preferencesDataStore("token")
//        return UserPreferences.getInstance(database)
//    }
// }
