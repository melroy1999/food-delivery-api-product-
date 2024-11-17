package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.model.Report;
import com.assignment.fooddelivery.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public Report updateReport(Long id, Report report) {
        Report existingReport = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        // Update other fields as necessary
        return reportRepository.save(existingReport);
    }

    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        reportRepository.delete(report);
    }
}
