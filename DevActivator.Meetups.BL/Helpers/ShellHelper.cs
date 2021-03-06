using System;
using System.Diagnostics;

namespace DevActivator.Meetups.BL.Helpers
{
    // todo: move from BL
    public static class ShellHelper
    {
        public static string Bash(this string command)
        {
            var escapedArgs = command;

            var process = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = "/bin/bash",
                    Arguments = $"-c \"{escapedArgs}\"",
                    RedirectStandardOutput = true,
                    UseShellExecute = false,
                    CreateNoWindow = true,
                }
            };

            string result;
            try
            {
                process.Start();
                result = process.StandardOutput.ReadToEnd();
                process.WaitForExit();
            }
            catch (Exception)
            {
                result = "bash error";
            }

            return result.TrimEnd('\r', '\n');
        }
    }
}