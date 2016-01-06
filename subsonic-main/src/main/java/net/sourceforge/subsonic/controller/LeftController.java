/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

/**
 * Controller for the left index frame.
 *
 * @author Sindre Mehus
 */
public class LeftController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SecurityService securityService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean musicFolderChanged = saveSelectedMusicFolder(request);
        Map<String, Object> map = new HashMap<String, Object>();

        String username = securityService.getCurrentUsername(request);
        List<MusicFolder> allMusicFolders = settingsService.getMusicFoldersForUser(username);
        MusicFolder selectedMusicFolder = settingsService.getSelectedMusicFolder(username);

        map.put("musicFolders", allMusicFolders);
        map.put("selectedMusicFolder", selectedMusicFolder);
        map.put("musicFolderChanged", musicFolderChanged);
        map.put("brand", settingsService.getBrand());
        map.put("user", securityService.getCurrentUser(request));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private boolean saveSelectedMusicFolder(HttpServletRequest request) {
        if (request.getParameter("musicFolderId") == null) {
            return false;
        }
        int musicFolderId = Integer.parseInt(request.getParameter("musicFolderId"));

        // Note: UserSettings.setChanged() is intentionally not called. This would break browser caching
        // of the left frame.
        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        settings.setSelectedMusicFolderId(musicFolderId);
        settingsService.updateUserSettings(settings);

        return true;
    }


    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}
