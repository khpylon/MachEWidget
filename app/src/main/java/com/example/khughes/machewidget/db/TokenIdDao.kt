package com.example.khughes.machewidget.db

import androidx.room.*
import com.example.khughes.machewidget.TokenId

@Dao
interface TokenIdDao {
    @Insert
    fun insertTokenId(info: TokenId)

    @Update
    fun updateTokenId(info: TokenId)

    @Delete
    fun deleteTokenId(info: TokenId)

//    @Query("DELETE FROM tokenid_info")
//    fun deleteAllTokenIds()

    @Query("DELETE FROM tokenid_info WHERE users = 0")
    fun deleteUnusedTokenIds()

    @Query("DELETE FROM tokenid_info WHERE tokenId LIKE :tokenId")
    fun deleteByTokenId(tokenId: String)

    @Query("SELECT * FROM tokenid_info")
    fun findTokenIds(): List<TokenId>

    @Query("SELECT * FROM tokenid_info WHERE tokenId LIKE :tokenId")
    fun findTokenId(tokenId: String): TokenId?

    @Query("UPDATE tokenid_info SET programState = :state WHERE tokenId = :tokenId")
    fun updateProgramState(state: String, tokenId: String)
}