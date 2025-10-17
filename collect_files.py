#!/usr/bin/env python3
"""
File Contents Collector
This script reads all files in the current directory and subdirectories,
then saves their contents to a single text file with file path information.
"""

import os
import sys
from pathlib import Path
from datetime import datetime

def is_text_file(file_path):
    """
    Check if a file is likely a text file by examining its extension
    and attempting to read a small portion as text.
    """
    # Common text file extensions
    text_extensions = {
        '.txt', '.py', '.js', '.html', '.css', '.json', '.xml', '.yml', '.yaml',
        '.md', '.rst', '.csv', '.sql', '.sh', '.bat', '.ps1', '.php', '.java',
        '.c', '.cpp', '.h', '.hpp', '.cs', '.rb', '.go', '.rs', '.swift',
        '.kt', '.scala', '.pl', '.r', '.m', '.lua', '.vim', '.ini', '.cfg',
        '.conf', '.log', '.dockerfile', '.gitignore', '.gitattributes'
    }
    
    file_path = Path(file_path)
    
    # Check extension first
    if file_path.suffix.lower() in text_extensions:
        return True
    
    # If no extension or unknown extension, try to read a small portion
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            f.read(1024)  # Try to read first 1KB as text
        return True
    except (UnicodeDecodeError, PermissionError):
        return False

def collect_files(directory_path):
    """
    Recursively collect all files from the directory and subdirectories.
    Returns a list of file paths.
    """
    files = []
    directory = Path(directory_path)
    
    try:
        for item in directory.rglob('*'):
            if item.is_file():
                files.append(item)
    except PermissionError as e:
        print(f"Permission denied accessing: {e}")
    
    return sorted(files)

def read_file_content(file_path):
    """
    Read the content of a file, handling various encodings and errors.
    """
    encodings = ['utf-8', 'latin-1', 'cp1252', 'ascii']
    
    for encoding in encodings:
        try:
            with open(file_path, 'r', encoding=encoding) as f:
                return f.read()
        except (UnicodeDecodeError, UnicodeError):
            continue
        except PermissionError:
            return "[Permission Denied - Cannot read file]"
        except Exception as e:
            return f"[Error reading file: {str(e)}]"
    
    return "[Binary file or unsupported encoding]"

def main():
    """
    Main function to collect and save file contents.
    """
    current_dir = Path.cwd()
    output_file = current_dir / "collected_files_content.txt"
    
    print(f"Collecting files from: {current_dir}")
    print(f"Output will be saved to: {output_file}")
    
    # Collect all files
    all_files = collect_files(current_dir)
    
    # Filter out the output file itself to avoid infinite loop
    files_to_process = [f for f in all_files if f != output_file]
    
    print(f"Found {len(files_to_process)} files to process...")
    
    # Create the output file
    try:
        with open(output_file, 'w', encoding='utf-8') as out_file:
            # Write header
            out_file.write("=" * 80 + "\n")
            out_file.write("FILE CONTENTS COLLECTION\n")
            out_file.write(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
            out_file.write(f"Source directory: {current_dir}\n")
            out_file.write(f"Total files processed: {len(files_to_process)}\n")
            out_file.write("=" * 80 + "\n\n")
            
            processed_count = 0
            skipped_count = 0
            
            for file_path in files_to_process:
                relative_path = file_path.relative_to(current_dir)
                
                # Check if it's likely a text file
                if is_text_file(file_path):
                    print(f"Processing: {relative_path}")
                    
                    # Write file header
                    out_file.write("\n" + "=" * 60 + "\n")
                    out_file.write(f"FILE: {relative_path}\n")
                    out_file.write(f"FULL PATH: {file_path}\n")
                    out_file.write(f"SIZE: {file_path.stat().st_size} bytes\n")
                    out_file.write("=" * 60 + "\n\n")
                    
                    # Read and write file content
                    content = read_file_content(file_path)
                    out_file.write(content)
                    out_file.write("\n\n")
                    
                    processed_count += 1
                else:
                    print(f"Skipping binary file: {relative_path}")
                    skipped_count += 1
            
            # Write summary
            out_file.write("\n" + "=" * 80 + "\n")
            out_file.write("SUMMARY\n")
            out_file.write("=" * 80 + "\n")
            out_file.write(f"Files processed: {processed_count}\n")
            out_file.write(f"Files skipped (binary): {skipped_count}\n")
            out_file.write(f"Total files found: {len(files_to_process)}\n")
            out_file.write(f"Output saved to: {output_file}\n")
            out_file.write("=" * 80 + "\n")
    
    except Exception as e:
        print(f"Error creating output file: {e}")
        sys.exit(1)
    
    print(f"\nCompleted successfully!")
    print(f"Processed: {processed_count} files")
    print(f"Skipped: {skipped_count} binary files")
    print(f"Output saved to: {output_file}")

if __name__ == "__main__":
    main()