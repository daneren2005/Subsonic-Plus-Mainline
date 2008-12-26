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

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.support.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Controller for the status page.
 *
 * @author Sindre Mehus
 */
public class StatusController extends ParameterizableViewController {

    private StatusService statusService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<TransferStatus> streamStatuses = statusService.getAllStreamStatuses();
        List<TransferStatus> downloadStatuses = statusService.getAllDownloadStatuses();
        List<TransferStatus> uploadStatuses = statusService.getAllUploadStatuses();

        Locale locale = RequestContextUtils.getLocale(request);
        List<TransferStatusHolder> transferStatuses = new ArrayList<TransferStatusHolder>();

        for (int i = 0; i < streamStatuses.size(); i++) {
            transferStatuses.add(new TransferStatusHolder(streamStatuses.get(i), true, false, false, i, locale));
        }
        for (int i = 0; i < downloadStatuses.size(); i++) {
            transferStatuses.add(new TransferStatusHolder(downloadStatuses.get(i), false, true, false, i, locale));
        }
        for (int i = 0; i < uploadStatuses.size(); i++) {
            transferStatuses.add(new TransferStatusHolder(uploadStatuses.get(i), false, false, true, i, locale));
        }

        map.put("transferStatuses", transferStatuses);
        map.put("chartWidth", StatusChartController.IMAGE_WIDTH);
        map.put("chartHeight", StatusChartController.IMAGE_HEIGHT);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public static class TransferStatusHolder {
        private TransferStatus transferStatus;
        private boolean isStream;
        private boolean isDownload;
        private boolean isUpload;
        private int index;
        private Locale locale;

        public TransferStatusHolder(TransferStatus transferStatus, boolean isStream, boolean isDownload, boolean isUpload,
                                    int index, Locale locale) {
            this.transferStatus = transferStatus;
            this.isStream = isStream;
            this.isDownload = isDownload;
            this.isUpload = isUpload;
            this.index = index;
            this.locale = locale;
        }

        public boolean isStream() {
            return isStream;
        }

        public boolean isDownload() {
            return isDownload;
        }

        public boolean isUpload() {
            return isUpload;
        }

        public int getIndex() {
            return index;
        }

        public Player getPlayer() {
            return transferStatus.getPlayer();
        }

        public String getPlayerType() {
            Player player = transferStatus.getPlayer();
            return player == null ? null : player.getType();
        }

        public String getUsername() {
            Player player = transferStatus.getPlayer();
            return player == null ? null : player.getUsername();
        }

        public String getPath() {
            File file = transferStatus.getFile();
            return file == null ? null : file.getPath();
        }

        public String getBytes() {
            return StringUtil.formatBytes(transferStatus.getBytesTransfered(), locale);
        }
    }

}
