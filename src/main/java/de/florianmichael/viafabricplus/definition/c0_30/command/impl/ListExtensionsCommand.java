/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.definition.c0_30.command.impl;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.definition.c0_30.command.ICommand;
import de.florianmichael.viafabricplus.injection.access.IExtensionProtocolMetadataStorage;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;

public class ListExtensionsCommand implements ICommand {
    @Override
    public String name() {
        return "listextensions";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void execute(String[] args) throws Exception {
        final UserConnection connection = currentViaConnection();
        if (!connection.has(ExtensionProtocolMetadataStorage.class)) {
            this.sendFeedback(Formatting.RED + "This command is only for " + LegacyProtocolVersion.c0_30cpe.getName());
            return;
        }
        ((IExtensionProtocolMetadataStorage) connection.get(ExtensionProtocolMetadataStorage.class)).getServerExtensions().forEach((extension, version) -> {
            this.sendFeedback(Formatting.GREEN + extension.getName() + Formatting.GOLD + " v" + version);
        });
    }
}
