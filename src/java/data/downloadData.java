/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import entitites.Company;
import entitites.Share;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Kingu
 */
public class downloadData {

    final int STALA_DLA_BANKIER = 44; //Stała wynikająca z konstrukcji strony HTML, od tego wiersza są interesujące informacje
    final int LICZBA_PRZESKOKU = 9; //Stała wynikająca z konstrukcji strony HTML, jest to przeskok omijajacy nieinteresujace nas dane

    private List<Share> downloadedShares;
    private List<Company> downloadedCompanies;

    private Document downloadedSite;
    private Elements downloadedSiteElements;

    private void downloadSite(String siteAddress) throws Exception {
        downloadedSite = Jsoup.connect("http://www.bankier.pl/inwestowanie/profile/quote.html?symbol=WIG30").get();
        downloadedSiteElements = downloadedSite.select("tbody td");
    }

    public void saveToCorrespondingLists() {
        downloadedCompanies = new ArrayList<>();
        downloadedShares = new ArrayList<>();
        try {
            
            downloadSite("");
            Share share;
            Company company;

            for (int i = STALA_DLA_BANKIER; i < downloadedSiteElements.size(); i += LICZBA_PRZESKOKU) {
                company = new Company();
                company.setName(downloadedSiteElements.get(i).text());
                company.setSymbol(downloadedSiteElements.get(i + 1).text());
                //TODO setStock

                share = new Share();
                share.setProbeDate(new Date());
                share.setValue(Double.valueOf(downloadedSiteElements.get(i + 2).text().replaceAll(",", ".")));
                share.setCompany(company);

                downloadedCompanies.add(company);
                downloadedShares.add(share);
            }
            /*
            for (int i = 0; i < downloadedCompanies.size(); i++) {
                System.out.println(downloadedCompanies.get(i).getName() + " (" + downloadedCompanies.get(i).getSymbol() + "): " + downloadedShares.get(i).getValue());
            }
*/
        } catch (Exception e) {
            System.out.println("Error in runWIG30Check: " + e.getMessage());
        }
    }

    public List<Share> getDownloadedShares() {
        return downloadedShares;
    }

    public List<Company> getDownloadedCompanies() {
        return downloadedCompanies;
    }
    
    
}
