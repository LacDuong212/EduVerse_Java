package com.eduverse.eduversebe.common.utils;

import java.util.*;
import java.util.stream.Collectors;

public class TfIdfCalculator {

    private final List<List<String>> documents = new ArrayList<>();

    // Thêm văn bản vào corpus (tập dữ liệu)
    public void addDocument(String text) {
        this.documents.add(tokenize(text));
    }

    // Tách từ (Tokenize): Chuyển về chữ thường, bỏ ký tự đặc biệt, tách khoảng trắng
    private List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        return Arrays.stream(text.toLowerCase().split("\\W+")) // Split non-word characters
                .filter(w -> !w.isEmpty() && w.length() > 2) // Lọc từ quá ngắn
                .collect(Collectors.toList());
    }

    // Tính TF (Tần suất xuất hiện của term trong doc)
    private double tf(List<String> doc, String term) {
        long count = doc.stream().filter(c -> c.equalsIgnoreCase(term)).count();
        return (double) count / doc.size();
    }

    // Tính IDF (Nghịch đảo tần suất văn bản - độ hiếm của từ)
    private double idf(String term) {
        long count = documents.stream().filter(doc -> doc.contains(term)).count();
        return Math.log((double) documents.size() / (1 + count));
    }

    // Tính độ tương đồng giữa doc tại index 'targetIdx' và doc tại 'candidateIdx'
    public double calculateSimilarity(int targetIdx, int candidateIdx) {
        List<String> targetDoc = documents.get(targetIdx);
        List<String> candidateDoc = documents.get(candidateIdx);

        // Lấy danh sách các từ khóa (terms) độc nhất trong targetDoc
        Set<String> uniqueTerms = new HashSet<>(targetDoc);

        double score = 0.0;
        for (String term : uniqueTerms) {
            // Logic tương tự natural: Cộng dồn trọng số của các từ khớp
            // TF tính trên Candidate, IDF tính trên toàn bộ tập dữ liệu
            double tfVal = tf(candidateDoc, term);
            double idfVal = idf(term);
            score += (tfVal * idfVal);
        }
        return score;
    }
}
