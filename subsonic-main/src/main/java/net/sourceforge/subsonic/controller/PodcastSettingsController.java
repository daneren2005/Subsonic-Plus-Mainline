package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.PodcastService;
import net.sourceforge.subsonic.command.PodcastSettingsCommand;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the page used to administrate the Podcast receiver.
 *
 * @author Sindre Mehus
 */
public class PodcastSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private PodcastService podcastService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        PodcastSettingsCommand command = new PodcastSettingsCommand();

        command.setInterval(String.valueOf(settingsService.getPodcastUpdateInterval()));
        command.setEpisodeCount(String.valueOf(settingsService.getPodcastEpisodeCount()));
        command.setFolder(settingsService.getPodcastFolder());
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        PodcastSettingsCommand command = (PodcastSettingsCommand) comm;

        settingsService.setPodcastUpdateInterval(Integer.parseInt(command.getInterval()));
        settingsService.setPodcastEpisodeCount(Integer.parseInt(command.getEpisodeCount()));
        settingsService.setPodcastFolder(command.getFolder());
        settingsService.save();

        podcastService.schedule();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPodcastService(PodcastService podcastService) {
        this.podcastService = podcastService;
    }
}
