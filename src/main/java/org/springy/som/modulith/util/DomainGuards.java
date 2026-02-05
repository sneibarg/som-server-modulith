package org.springy.som.modulith.util;

import org.springy.som.modulith.exception.area.InvalidAreaException;
import org.springy.som.modulith.exception.character.InvalidPlayerCharacterException;
import org.springy.som.modulith.exception.clazz.InvalidRomClassException;
import org.springy.som.modulith.exception.command.InvalidCommandException;
import org.springy.som.modulith.exception.item.InvalidItemException;
import org.springy.som.modulith.exception.mobile.InvalidMobileException;
import org.springy.som.modulith.exception.player.InvalidPlayerException;
import org.springy.som.modulith.exception.race.InvalidRomRaceException;
import org.springy.som.modulith.exception.reset.InvalidResetException;
import org.springy.som.modulith.exception.room.InvalidRoomException;
import org.springy.som.modulith.exception.shop.InvalidShopException;
import org.springy.som.modulith.exception.special.InvalidSpecialException;

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

    public static Supplier<InvalidRomClassException> romClassMissing() {
        return () -> new InvalidRomClassException("RomClassDocument must be provided");
    }

    public static Supplier<InvalidRomClassException> romClassIdMissing() {
        return () -> new InvalidRomClassException("RomClassDocument id must be provided");
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
