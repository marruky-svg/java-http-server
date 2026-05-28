package com.marruky.Http;

public class FastaValidator {
    public static class ValidationResult {
        private boolean valid;
        private String errorMensage;

        public ValidationResult(boolean valid, String errorMensage) {
            this.valid = valid;
            this.errorMensage = errorMensage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMensage() {
            return errorMensage;
        }
    }

    public ValidationResult validade(String fasta, int exepectedSequences) {
        if (fasta == null || fasta.isBlank()) {
            return new ValidationResult(false, "Empty FASTA");
        }
        int numSequences = 0;
        for (char c : fasta.toCharArray()) {
            if (c == '>') {
                numSequences++;
            }
        }
        if (numSequences == 0) {
            return new ValidationResult(false, "No sequences found");
        } else if (numSequences != exepectedSequences) {
            return new ValidationResult(false, "Expected " + exepectedSequences + ",got " + numSequences);
        }

        for (String linha : fasta.split("\n")) {
            if (linha.startsWith(">")) {
                continue;
            }
            for(char c : linha.toCharArray()) {
                if(c != 'A' && c != 'G' && c != 'T' && c != 'C' && c != 'N') {
                    return new ValidationResult(false, "Invalid base: " + c);
                }
            }
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validade(String fasta){
        if (fasta == null || fasta.isBlank()) {
            return new ValidationResult(false, "Empty FASTA");
        }
        int numSequences = 0;
        for (char c : fasta.toCharArray()) {
            if (c == '>') {
                numSequences++;
            }
        }
        if (numSequences == 0) {
            return new ValidationResult(false, "No sequences found");
        }
        for(String linha : fasta.split("\n")) {
            if (linha.startsWith(">")) {
                continue;
            }
            for(char c : linha.toCharArray()) {
                if(c != 'A' && c != 'G' && c != 'T' && c != 'C' && c != 'N') {
                    return new ValidationResult(false, "Invalid base: " + c);
                }
            }
        }
        return new ValidationResult(true, "");
    }
}
