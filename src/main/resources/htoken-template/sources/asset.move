//MODULE_NAME=asset
//COIN_TYPE=ASSET
//COIN_SYMBOL=hSUI-USDC
//COIN_DECIMALS=6

module htoken::${MODULE_NAME} {
    use sui::coin;

    public struct ${COIN_TYPE} has drop {}

    fun init(witness: ${COIN_TYPE}, ctx: &mut TxContext) {
        let (cap, metadata) = coin::create_currency<${COIN_TYPE}>(
            witness,
            ${COIN_DECIMALS},
            b"${COIN_SYMBOL}",
            b"${COIN_SYMBOL} Coin",
            b"${COIN_SYMBOL} Coin - yield-bearing representation.",
            std::option::none<sui::url::Url>(),
            ctx,
        );

        sui::transfer::public_freeze_object(metadata);
        sui::transfer::public_transfer(cap, ctx.sender());
    }
}
