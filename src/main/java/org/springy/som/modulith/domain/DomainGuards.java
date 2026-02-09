package org.springy.som.modulith.domain;

import org.springy.som.modulith.domain.area.internal.InvalidAreaException;
import org.springy.som.modulith.domain.character.internal.InvalidPlayerCharacterException;
import org.springy.som.modulith.domain.clazz.internal.InvalidClassException;
import org.springy.som.modulith.domain.command.internal.InvalidCommandException;
import org.springy.som.modulith.domain.item.internal.InvalidItemException;
import org.springy.som.modulith.domain.mobile.internal.InvalidMobileException;
import org.springy.som.modulith.domain.player.internal.InvalidPlayerException;
import org.springy.som.modulith.domain.race.internal.InvalidRomRaceException;
import org.springy.som.modulith.domain.reset.internal.InvalidResetException;
import org.springy.som.modulith.domain.room.internal.InvalidRoomException;
import org.springy.som.modulith.domain.shop.internal.InvalidShopException;
import org.springy.som.modulith.domain.special.internal.InvalidSpecialException;

import java.util.function.Supplier;

public final class DomainGuards {
    private DomainGuards() {}

    public static Supplier<InvalidAreaException> areaMissing() {
        return () -> new InvalidAreaException("AreaDocument must be provided");
    }

    public static Supplier<InvalidAreaException> areaIdMissing() {
        return () -> new InvalidAreaException("AreaDocument id must be provided");
    }

    public static Supplier<InvalidPlayerCharacterException> playerCharacterMissing() {
        return () -> new InvalidPlayerCharacterException("CharacterDocument must be provided");
    }

    public static Supplier<InvalidPlayerCharacterException> playerCharacterIdMissing() {
        return () -> new InvalidPlayerCharacterException("CharacterDocument id must be provided");
    }

    public static Supplier<InvalidClassException> romClassMissing() {
        return () -> new InvalidClassException("ClassDocument must be provided");
    }

    public static Supplier<InvalidClassException> romClassIdMissing() {
        return () -> new InvalidClassException("ClassDocument id must be provided");
    }

    public static Supplier<InvalidCommandException> commandMissing() {
        return () -> new InvalidCommandException("ROM command must be provided");
    }

    public static Supplier<InvalidCommandException> commandIdMissing() {
        return () -> new InvalidCommandException("ROM command id must be provided");
    }

    public static Supplier<InvalidItemException> itemMissing() {
        return () -> new InvalidItemException("ROM item must be provided");
    }

    public static Supplier<InvalidItemException> itemIdMissing() {
        return () -> new InvalidItemException("ROM item id must be provided");
    }

    public static Supplier<InvalidMobileException> mobileMissing() {
        return () -> new InvalidMobileException("ROM mobile must be provided");
    }

    public static Supplier<InvalidMobileException> mobileIdMissing() {
        return () -> new InvalidMobileException("ROM mobile id must be provided");
    }

    public static Supplier<InvalidPlayerException> playerAccountMissing() {
        return () -> new InvalidPlayerException("ROM account must be provided");
    }

    public static Supplier<InvalidPlayerException> playerAccountIdMissing() {
        return () -> new InvalidPlayerException("ROM account id must be provided");
    }

    public static Supplier<InvalidRomRaceException> romRaceMissing() {
        return () -> new InvalidRomRaceException("ROM race must be provided");
    }

    public static Supplier<InvalidRomRaceException> romRaceIdMissing() {
        return () -> new InvalidRomRaceException("ROM race id must be provided");
    }

    public static Supplier<InvalidResetException> resetMissing() {
        return () -> new InvalidResetException("ROM reset must be provided");
    }

    public static Supplier<InvalidResetException> resetIdMissing() {
        return () -> new InvalidResetException("ROM reset id must be provided");
    }

    public static Supplier<InvalidRoomException> roomMissing() {
        return () -> new InvalidRoomException("ROM room must be provided");
    }

    public static Supplier<InvalidRoomException> roomIdMissing() {
        return () -> new InvalidRoomException("ROM room id must be provided");
    }

    public static Supplier<InvalidShopException> shopMissing() {
        return () -> new InvalidShopException("ROM shop must be provided");
    }

    public static Supplier<InvalidShopException> shopIdMissing() {
        return () -> new InvalidShopException("ROM shop id must be provided");
    }

    public static Supplier<InvalidSpecialException> specialMissing() {
        return () -> new InvalidSpecialException("ROM special must be provided");
    }

    public static Supplier<InvalidSpecialException> specialIdMissing() {
        return () -> new InvalidSpecialException("ROM special id must be provided");
    }
}
